package com.cool.request.plugin.apipost;

import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.bean.components.controller.CustomController;
import com.cool.request.common.cache.CacheStorageService;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.common.state.ThirdPartyPersistent;
import com.cool.request.component.api.export.ApiExport;
import com.cool.request.component.api.export.ExportCondition;
import com.cool.request.component.http.net.FormDataInfo;
import com.cool.request.component.http.net.HTTPResponseBody;
import com.cool.request.component.http.net.KeyValue;
import com.cool.request.lib.springmvc.Body;
import com.cool.request.lib.springmvc.FormBody;
import com.cool.request.lib.springmvc.FormUrlBody;
import com.cool.request.lib.springmvc.JSONBody;
import com.cool.request.utils.*;
import com.cool.request.utils.param.HTTPParameterProvider;
import com.cool.request.view.dialog.SettingDialog;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApipostExport implements ApiExport {
    private final Project project;

    public ApipostExport(Project project) {
        this.project = project;
    }

    @Override
    public boolean export(String json) {
        return false;
    }

    @Override
    public boolean canExport() {
        try {
            if (StringUtils.isEmpty(ThirdPartyPersistent.getInstance().apipostHost) ||
                    StringUtils.isEmpty(ThirdPartyPersistent.getInstance().apipostToken)) {
                return false;
            }
            ApipostProjectResponse apipostProjectResponse = new ApipostAPI().listProject();
            return apipostProjectResponse != null && apipostProjectResponse.getCode().equals(10000);
        } catch (IOException ignored) {
        }
        return false;

    }

    @Override
    public void showCondition() {
        Configurable[] newConfigurable = SettingDialog.createNewConfigurable(project);
        SettingDialog.show(project, newConfigurable, 1);
    }

    @Override
    public Map<String, Boolean> checkToken(ExportCondition exportCondition) {
        return null;
    }

    private List<ParameterDTO> buildParameterFromKeyValue(List<KeyValue> keyValues) {
        List<ParameterDTO> result = new ArrayList<>();
        for (KeyValue keyValue : keyValues) {
            result.add(ParameterDTO.ParameterDTOBuilder.aParameterDTO()
                    .withKey(keyValue.getKey())
                    .withValue(keyValue.getValue())
                    .withType(keyValue.getValueType())
                    .build());
        }
        return result;

    }

    private List<ParameterDTO> buildParameterFromFormKeyValue(List<FormDataInfo> keyValues) {
        List<ParameterDTO> result = new ArrayList<>();
        for (FormDataInfo keyValue : keyValues) {
            result.add(ParameterDTO.ParameterDTOBuilder.aParameterDTO()
                    .withKey(keyValue.getName())
                    .withValue(keyValue.getValue())
                    .withType(keyValue.getType())
                    .build());
        }
        return result;

    }

    private BodyDTO buildBodyDTO(Controller controller,
                                 HTTPParameterProvider httpParameterProvider,
                                 RequestEnvironment selectRequestEnvironment) {
        Body body = httpParameterProvider.getBody(project, controller, selectRequestEnvironment);
        BodyDTO.BodyDTOBuilder bodyDTOBuilder = BodyDTO.BodyDTOBuilder.aBodyDTO();

        if (body instanceof FormUrlBody) {
            bodyDTOBuilder.withMode("urlencoded");
            bodyDTOBuilder.withParameter(buildParameterFromKeyValue(((FormUrlBody) body).getData()));
            return bodyDTOBuilder.build();
        }
        if (body instanceof JSONBody) {
            bodyDTOBuilder.withMode("json");
            bodyDTOBuilder.withRaw(((JSONBody) body).getValue());
            return bodyDTOBuilder.build();
        }
        if (body instanceof FormBody) {
            bodyDTOBuilder.withMode("form-data");
            bodyDTOBuilder.withParameter(buildParameterFromFormKeyValue(((FormBody) body).getData()));
            return bodyDTOBuilder.build();
        }
        bodyDTOBuilder.withMode("none");
        return bodyDTOBuilder.build();
    }

    private String getUrl(Controller controller,
                          HTTPParameterProvider httpParameterProvider,
                          RequestEnvironment selectRequestEnvironment) {
        if (controller instanceof CustomController) return controller.getUrl();
        return httpParameterProvider.getFullUrl(project, controller, selectRequestEnvironment);
    }

    private String getPath(Controller controller,
                           HTTPParameterProvider httpParameterProvider,
                           RequestEnvironment selectRequestEnvironment) {
        if (controller instanceof CustomController) return controller.getUrl();
        String fullUrl = httpParameterProvider.getFullUrl(project, controller, selectRequestEnvironment);
        return StringUtils.removeHostFromUrl(fullUrl);
    }


    private RequestDTO buildRequestDTO(Controller controller,
                                       HTTPParameterProvider httpParameterProvider,
                                       RequestEnvironment selectRequestEnvironment) {

        return RequestDTO.RequestDTOBuilder.aRequestDTO()
                .withUrl(getUrl(controller, httpParameterProvider, selectRequestEnvironment))
                .withPath(getPath(controller, httpParameterProvider, selectRequestEnvironment))
                .withPreUrl("")
                .withModule("")
                .withBody(buildBodyDTO(controller, httpParameterProvider, selectRequestEnvironment))
                .withQuery(QueryDTO.QueryDTOBuilder.aQueryDTO()
                        .withParameter(buildParameterFromKeyValue(httpParameterProvider.getUrlParam(project, controller, selectRequestEnvironment)))
                        .build())
                .withHeader(HeaderDTO.HeaderDTOBuilder.aHeaderDTO()
                        .withParameter(buildParameterFromKeyValue(httpParameterProvider.getHeader(project, controller, selectRequestEnvironment)))
                        .build())
                .build();
    }

    private List<ApisDTO> buildApis(List<Controller> controllers) {
        List<ApisDTO> apisDTOS = new ArrayList<>();
        RequestEnvironment selectRequestEnvironment = project.getUserData(CoolRequestConfigConstant.RequestEnvironmentProvideKey).getSelectRequestEnvironment();

        for (Controller controller : controllers) {
            HTTPParameterProvider panelParameterProvider = PanelParameterProviderUtils.getPanelParameterProvider(project, controller);

            ApisDTO.ApisDTOBuilder apisDTOBuilder = ApisDTO.ApisDTOBuilder.anApisDTO()
                    .withMethod(panelParameterProvider.getHttpMethod(project, controller, selectRequestEnvironment).toString())
                    .withName(controller.getUrl())
                    .withRequest(buildRequestDTO(controller, panelParameterProvider, selectRequestEnvironment));

            CacheStorageService service = ApplicationManager.getApplication().getService(CacheStorageService.class);
            HTTPResponseBody responseCache = service.getResponseCache(controller.getId());
            if (responseCache != null) {
                ResponseDTO responseDTO = new ResponseDTO();
                SuccessDTO successDTO = new SuccessDTO();
                byte[] response = Base64Utils.decode(responseCache.getBase64BodyData());
                successDTO.setRaw(new String(response, StandardCharsets.UTF_8));
                responseDTO.setSuccess(successDTO);
                apisDTOBuilder.withResponse(responseDTO);
            }
            apisDTOS.add(apisDTOBuilder.build());

        }
        return apisDTOS;
    }

    private ApipostControllerRequestBody buildApipostControllerRequestBody(List<Controller> controllers,
                                                                           String project,
                                                                           String folderId) {
        return ApipostControllerRequestBody.ApipostControllerRequestBodyBuilder.anApipostControllerRequestBody()
                .withProjectId(project)
                .withFolder(folderId)
                .withApis(buildApis(controllers))
                .build();
    }

    private void doExport(List<Controller> controllers, String projectId, String folderId) {
        ApipostAPI apipostAPI = new ApipostAPI();
        ProgressWindowWrapper.newProgressWindowWrapper(project).run(new Task.Backgroundable(project, "") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    ApipostExportResponse export = apipostAPI.export(buildApipostControllerRequestBody(controllers, projectId, folderId));
                    if (export != null && export.getCode().equals(10000)) {
                        MessagesWrapperUtils.showOkCancelDialog("导出成功,共" + export.getData().getSuccessApis().size() + "个", "提示", CoolRequestIcons.APIPOST);
                    } else {
                        if (export != null) {
                            MessagesWrapperUtils.showErrorDialog(export.getMsg(), "提示");
                        }
                    }
                } catch (Exception ignored) {

                }
            }
        });
    }

    @Override
    public boolean export(List<Controller> controllers) {
        SwingUtilities.invokeLater(() -> ApipostProjectFolderSelectDialog.showDialog(project, (project, folderId) -> {
            doExport(controllers, project, folderId);
        }));
        return false;
    }
}
