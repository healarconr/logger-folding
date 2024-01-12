package com.github.healarconr.loggerfolding;

import com.intellij.application.options.editor.CodeFoldingOptionsProvider;
import com.intellij.openapi.options.BeanConfigurable;

/**
 * Code folding options provider to enable collapse by default on logger method calls
 *
 * @author <a href="mailto:hernaneduardoalarcon@gmail.com">Hernán Alarcón</a>
 */
public class FoldingOptionsProvider extends BeanConfigurable<LoggerFoldingApplicationSettings.State> implements CodeFoldingOptionsProvider {

    protected FoldingOptionsProvider() {

        super(LoggerFoldingApplicationSettings.getInstance().getState());
        checkBox("Logger method calls", LoggerFoldingApplicationSettings.getInstance()
                .getState()::getCollapseByDefault, LoggerFoldingApplicationSettings
                .getInstance().getState()::setCollapseByDefault);
    }

}
