package com.github.healarconr.loggerfolding;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.TableUtil;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Configurable used to let the user establish the names of the logger classes to fold/unfold using the IDE settings (Tools > Logger folding)
 *
 * @author <a href="mailto:hernaneduardoalarcon@gmail.com">Hernán Alarcón</a>
 */
public class LoggerFoldingConfigurable implements Configurable {

  private final Project project;

  private CanonicalNamesTableModel canonicalNamesTableModel;
  private JBTable canonicalNamesTable;

  public LoggerFoldingConfigurable(Project project) {
    this.project = project;
  }

  @Nls
  @Override
  public String getDisplayName() {
    return "Logger folding";
  }

  @Nullable
  @Override
  public String getHelpTopic() {
    return null;
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    JPanel container = new JPanel(new BorderLayout());

    JPanel canonicalNamesPanel = new JPanel(new BorderLayout());
    container.add(canonicalNamesPanel, BorderLayout.CENTER);

    canonicalNamesPanel.setBorder(IdeBorderFactory.createTitledBorder("Classes or Interfaces to Fold/Unfold", false));

    canonicalNamesTableModel = new CanonicalNamesTableModel(Collections.emptyList());
    canonicalNamesTable = new JBTable(canonicalNamesTableModel);

    JPanel canonicalNamesTablePanel = ToolbarDecorator.createDecorator(canonicalNamesTable).setAddAction(anActionButton -> {
      int rowIndex = canonicalNamesTable.getSelectedRow() + 1;
      if (rowIndex < 0) {
        rowIndex = canonicalNamesTableModel.getRowCount();
      }
      canonicalNamesTableModel.addEmptyRow(rowIndex);
      canonicalNamesTableModel.fireTableRowsInserted(rowIndex, rowIndex);
      canonicalNamesTable.setRowSelectionInterval(rowIndex, rowIndex);
      TableUtil.editCellAt(canonicalNamesTable, rowIndex, 0);
      Component editorComponent = canonicalNamesTable.getEditorComponent();
      if (editorComponent != null) {
        editorComponent.requestFocus();
      }
    }).setRemoveAction(anActionButton -> {
      int rowIndex = canonicalNamesTable.getSelectedRow();
      if (rowIndex < 0) {
        return;
      }
      TableUtil.stopEditing(canonicalNamesTable);
      canonicalNamesTableModel.remove(rowIndex);
      canonicalNamesTableModel.fireTableRowsDeleted(rowIndex, rowIndex);
      if (rowIndex >= canonicalNamesTableModel.getRowCount()) {
        rowIndex--;
      }
      if (rowIndex >= 0) {
        canonicalNamesTable.setRowSelectionInterval(rowIndex, rowIndex);
      }
    }).createPanel();

    canonicalNamesPanel.add(canonicalNamesTablePanel, BorderLayout.CENTER);

    return container;
  }

  @Override
  public boolean isModified() {

    LoggerFoldingProjectSettings.State state = new LoggerFoldingProjectSettings.State();
    state.setCanonicalNames(canonicalNamesTableModel.getCanonicalNames());
    return !state.equals(LoggerFoldingProjectSettings.getInstance(project).getState());
  }

  @Override
  public void apply() {
    TableUtil.stopEditing(canonicalNamesTable);
    LoggerFoldingProjectSettings.getInstance(project).getState().setCanonicalNames(canonicalNamesTableModel.getCanonicalNames());
  }

  @Override
  public void reset() {
    canonicalNamesTableModel.setCanonicalNames(LoggerFoldingProjectSettings.getInstance(project).getState().getCanonicalNames());
    canonicalNamesTableModel.fireTableDataChanged();
  }

  /**
   * Table model used to store the canonical names of the logger classes
   *
   * @author <a href="mailto:hernaneduardoalarcon@gmail.com">Hernán Alarcón</a>
   */
  private static class CanonicalNamesTableModel extends AbstractTableModel {

    private List<String> canonicalNames;

    CanonicalNamesTableModel(List<String> canonicalNames) {
      setCanonicalNames(canonicalNames);
    }

    @Override
    public int getRowCount() {
      return canonicalNames.size();
    }

    @Override
    public int getColumnCount() {
      return 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      return canonicalNames.get(rowIndex);
    }

    @Override
    public String getColumnName(int column) {
      return "Canonical name";
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      canonicalNames.set(rowIndex, (String) aValue);
    }

    private void addEmptyRow(int index) {
      canonicalNames.add(index, "");
    }

    private void remove(int index) {
      canonicalNames.remove(index);
    }

    private List<String> getCanonicalNames() {
      return new ArrayList<>(canonicalNames);
    }

    void setCanonicalNames(List<String> canonicalNames) {
      this.canonicalNames = canonicalNames == null ? new ArrayList<>() : new ArrayList<>(canonicalNames);
    }
  }

}
