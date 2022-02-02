package org.formentor.magnolia.tokenizer.setup;

import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AddPermissionTask;
import info.magnolia.module.delta.AddURIPermissionTask;
import info.magnolia.module.delta.ArrayDelegateTask;
import info.magnolia.module.delta.BootstrapSingleModuleResource;
import info.magnolia.module.delta.NodeExistsDelegateTask;
import info.magnolia.module.delta.RemoveNodeTask;
import info.magnolia.module.delta.RemovePermissionTask;
import info.magnolia.module.delta.SetPropertyTask;
import info.magnolia.module.delta.Task;
import info.magnolia.repository.RepositoryConstants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This class is optional and lets you manage the versions of your module,
 * by registering "deltas" to maintain the module's configuration, or other type of content.
 * If you don't need this, simply remove the reference to this class in the module descriptor xml.
 *
 * @see info.magnolia.module.DefaultModuleVersionHandler
 * @see info.magnolia.module.ModuleVersionHandler
 * @see info.magnolia.module.delta.Task
 */
public class TokenizerModuleVersionHandler extends DefaultModuleVersionHandler {
    @Override
    protected List<Task> getExtraInstallTasks(InstallContext ctx) {
        return Arrays.asList(
                new ArrayDelegateTask("Grant GET on \"/*\" to anonymous role",
                    new RemovePermissionTask("Remove permissions GET on \"/*\" to anonymous role", "anonymous", "uri", "/*",  0),
                    new AddURIPermissionTask("Grant GET on \"/*\" to anonymous role", "anonymous", "/*", 8)
                ),
                new ArrayDelegateTask("Grant Read on workspace \"website\" to anonymous role",
                    new RemovePermissionTask("Remove permissions on workspace \"website\" to anonymous role", "anonymous", "website", "/*",  0),
                    new AddPermissionTask("Grant Read on workspace \"website\" to anonymous role", "anonymous", "website", "/*",  8, true)
                )
        );
    }
}
