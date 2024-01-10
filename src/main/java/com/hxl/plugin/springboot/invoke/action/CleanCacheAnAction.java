package com.hxl.plugin.springboot.invoke.action;

import com.hxl.plugin.springboot.invoke.utils.NotifyUtils;
import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.hxl.plugin.springboot.invoke.view.main.MainTopTreeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.util.ui.tree.TreeUtil;
import icons.MyIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;
import java.util.List;

public class CleanCacheAnAction  extends AnAction {
    private final SimpleTree simpleTree;
    private  final MainTopTreeView mainTopTreeView;
    public CleanCacheAnAction(MainTopTreeView mainTopTreeView) {
        super(ResourceBundleUtils.getString("clear.request.cache"));
        getTemplatePresentation().setIcon(MyIcons.DELETE);
        this.simpleTree = ((SimpleTree) mainTopTreeView.getTree());
        this.mainTopTreeView = mainTopTreeView;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        TreePath selectedPathIfOne = TreeUtil.getSelectedPathIfOne(this.simpleTree);
        if (selectedPathIfOne!=null && selectedPathIfOne.getLastPathComponent() instanceof MainTopTreeView.FeaturesModuleNode){
            String data = ((MainTopTreeView.FeaturesModuleNode) selectedPathIfOne.getLastPathComponent()).getData();
            if ("Controller".equalsIgnoreCase(data)){
                for (List<MainTopTreeView.RequestMappingNode> value : mainTopTreeView.getRequestMappingNodeMap().values()) {
                    for (MainTopTreeView.RequestMappingNode requestMappingNode : value) {
//                        clearRequestCache(requestMappingNode.getData());
                    }
                }
            }
        }
        if (selectedPathIfOne!=null && selectedPathIfOne.getLastPathComponent() instanceof MainTopTreeView.ClassNameNode){
            MainTopTreeView.ClassNameNode classNameNode = (MainTopTreeView.ClassNameNode) selectedPathIfOne.getLastPathComponent();
            for (MainTopTreeView.RequestMappingNode requestMappingNode : mainTopTreeView.getRequestMappingNodeMap().
                    getOrDefault(classNameNode, List.of())) {
//                clearRequestCache(requestMappingNode.getData());
            }
        }
        if (selectedPathIfOne!=null && selectedPathIfOne.getLastPathComponent() instanceof MainTopTreeView.RequestMappingNode){
            MainTopTreeView.RequestMappingNode requestMappingNode = (MainTopTreeView.RequestMappingNode) selectedPathIfOne.getLastPathComponent();
//            clearRequestCache(requestMappingNode.getData());

        }
        NotifyUtils.notification(mainTopTreeView.getProject(),"Clear Success");
    }
}
