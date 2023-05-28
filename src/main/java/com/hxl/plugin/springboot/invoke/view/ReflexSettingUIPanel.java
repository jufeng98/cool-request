package com.hxl.plugin.springboot.invoke.view;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.bean.BeanInvokeSetting;
import com.hxl.plugin.springboot.invoke.bean.SpringMvcRequestMappingEndpoint;
import com.hxl.plugin.springboot.invoke.invoke.RequestCache;
import com.hxl.plugin.springboot.invoke.utils.RequestParamCacheManager;
import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;

public class ReflexSettingUIPanel extends JPanel {
    private JRadioButton sourceButton;
    private JRadioButton proxyButton;
    private JCheckBox interceptor;
    private SpringMvcRequestMappingEndpoint springMvcRequestMappingEndpoint;

    public ReflexSettingUIPanel() {
        super(new BorderLayout());
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = JBUI.insets(5);

        JPanel proxyJPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        proxyButton = new JRadioButton(ResourceBundleUtils.getString("proxy.object"));
        sourceButton = new JRadioButton(ResourceBundleUtils.getString("source.object"));
        ButtonGroup proxyButtonGroup = new ButtonGroup();
        proxyButtonGroup.add(proxyButton);
        proxyButtonGroup.add(sourceButton);
        proxyJPanel.add(proxyButton);
        proxyJPanel.add(sourceButton);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        sourceButton.setSelected(true);
        panel.add(proxyJPanel, constraints);

        interceptor = new JCheckBox(ResourceBundleUtils.getString("interceptor"));
        JPanel webJPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        webJPanel.add(interceptor);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        panel.add(webJPanel, constraints);

        add(panel, BorderLayout.CENTER);
    }

    public void setRequestMappingInvokeBean(SpringMvcRequestMappingEndpoint springMvcRequestMappingEndpoint) {
        this.springMvcRequestMappingEndpoint = springMvcRequestMappingEndpoint;
        loadConfig();
    }
    private void createIsNotExist() {
        if (!Files.exists(Constant.CONFIG_CONTROLLER_SETTING)) {
            try {
                Files.createDirectory(Constant.CONFIG_CONTROLLER_SETTING);
            } catch (IOException ignored) {
            }
        }
    }

    private void loadConfig() {
        RequestCache cache = RequestParamCacheManager.getCache(this.springMvcRequestMappingEndpoint.getId());
        proxyButton.setSelected(cache != null && cache.isUseProxy());
        sourceButton.setSelected(cache != null && !cache.isUseProxy());
        interceptor.setSelected(cache != null && cache.isUseInterceptor());
    }

    public BeanInvokeSetting getBeanInvokeSetting() {
        BeanInvokeSetting beanInvokeSetting = new BeanInvokeSetting();
        beanInvokeSetting.setUseInterceptor(interceptor.isSelected());
        beanInvokeSetting.setUseProxy(proxyButton.isSelected());
        return beanInvokeSetting;
    }

}


