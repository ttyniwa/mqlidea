package ru.investflow.mql.runconfig;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.investflow.mql.runconfig.ui.MQL4CompilerRunnerEditor;

import javax.swing.JComponent;

public class MQL4RunCompilerConfiguration extends RunConfigurationBase {

    protected MQL4RunCompilerConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new MQL4CompilerRunnerEditor(this);
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        //todo:
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        return new RunProfileState() {
            @Nullable
            @Override
            public ExecutionResult execute(Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
                //todo:
                return null;
            }
        };
    }
}