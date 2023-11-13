package com.hxl.plugin.springboot.invoke.action.ui;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.view.events.IToolBarViewEvents;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAwareAction;
import org.jetbrains.annotations.NotNull;

/**
 * delete tree data
 */
public class CleanAction extends DumbAwareAction {
    private final IToolBarViewEvents iViewEvents;
    public CleanAction(IToolBarViewEvents iViewEvents) {
        super(() -> "Delete ALL", AllIcons.Actions.GC);
        this.iViewEvents=iViewEvents;
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        iViewEvents.clearTree();
        ApplicationManager.getApplication().getMessageBus().syncPublisher(IdeaTopic.DELETE_ALL_DATA).onDelete();
    }
}