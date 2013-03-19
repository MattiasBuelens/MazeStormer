package mazestormer.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.Beans;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.ButtonGroup;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.RowFilter;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import mazestormer.controller.ILogController;

public class LogPanel extends ViewPanel {

	private static final long serialVersionUID = 1L;

	private final ILogController controller;

	private JTable table;
	private LogTableModel tableModel;
	private LogTableRowFilter tableFilter;

	private ButtonGroup groupFilters;
	private JPopupMenu menuFilters;

	public LogPanel(ILogController controller) {
		this.controller = controller;

		setBorder(new TitledBorder(null, "Log", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new BorderLayout(0, 0));

		createTable();
		createToolbar();

		if (!Beans.isDesignTime())
			registerController();
	}

	private void registerController() {
		registerEventBus(controller.getEventBus());

		controller.addLogHandler(new LogHandler());
	}

	private void createToolbar() {
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		add(toolbar, BorderLayout.NORTH);

		toolbar.add(createFilter());
	}

	private SplitButton createFilter() {
		groupFilters = new ButtonGroup();
		menuFilters = new JPopupMenu();
		addPopup(this, menuFilters);

		SplitButton btnFilter = new SplitButton();
		btnFilter.setAlwaysDropDown(true);
		btnFilter.setText("Filter");
		btnFilter.setPopupMenu(menuFilters);

		// There is no list of known log levels,
		// so the default levels need to be hard-coded
		createFilterItem(Level.OFF);
		createFilterItem(Level.SEVERE);
		createFilterItem(Level.WARNING);
		createFilterItem(Level.INFO);
		createFilterItem(Level.CONFIG);
		createFilterItem(Level.FINE);
		createFilterItem(Level.FINER);
		createFilterItem(Level.FINEST);
		createFilterItem(Level.ALL);

		return btnFilter;
	}

	private void createFilterItem(final Level level) {
		final JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(level.getLocalizedName());
		menuItem.setSelected(level == tableFilter.getFilterLevel());
		menuItem.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					tableFilter.setFilterLevel(level);
					tableModel.fireTableDataChanged();
				}
			}
		});
		groupFilters.add(menuItem);
		menuFilters.add(menuItem);
	}

	private void createTable() {
		// Create models
		tableModel = new LogTableModel();
		tableFilter = new LogTableRowFilter();
		TableRowSorter<LogTableModel> sorter = new TableRowSorter<LogTableModel>(tableModel);
		sorter.setRowFilter(tableFilter);

		table = new JTable(tableModel);
		table.setRowSorter(sorter);
		table.setFillsViewportHeight(true);
		add(new JScrollPane(table));

		// Resize last column
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		// Set size of level column
		int levelIndex = tableModel.getColumnIndex(LogTableColumn.Level);
		TableColumn levelColumn = table.getColumnModel().getColumn(levelIndex);
		levelColumn.setPreferredWidth(100);
		levelColumn.setMaxWidth(100);

		// Scroll to last row when inserting new records
		table.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));
			}
		});
	}

	// Dummy method to trick the designer into showing the popup menus
	private static void addPopup(Component component, final JPopupMenu popup) {

	}

	private enum LogTableColumn {
		Level {
			@Override
			public Object getValue(LogRecord record) {
				if (record == null)
					return "";
				return record.getLevel().getLocalizedName();
			}
		},
		Message {
			@Override
			public Object getValue(LogRecord record) {
				if (record == null)
					return "";
				return record.getMessage();
			}
		};

		public abstract Object getValue(LogRecord record);
	}

	private class LogTableRowFilter extends RowFilter<LogTableModel, Integer> {

		private Level filterLevel = Level.ALL;

		public Level getFilterLevel() {
			return filterLevel;
		}

		public void setFilterLevel(Level filterLevel) {
			this.filterLevel = filterLevel;
		}

		@Override
		public boolean include(RowFilter.Entry<? extends LogTableModel, ? extends Integer> entry) {
			int levelIndex = entry.getModel().getColumnIndex(LogTableColumn.Level);
			Level entryLevel = Level.parse(entry.getStringValue(levelIndex));
			return filterLevel.intValue() <= entryLevel.intValue();
		}

	}

	private class LogHandler extends Handler {

		@Override
		public void publish(final LogRecord record) {
			if (!isLoggable(record))
				return;

			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					tableModel.add(record);
				}
			});
		}

		@Override
		public void flush() {

		}

		@Override
		public void close() throws SecurityException {

		}
	}

	private class LogTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		private List<LogRecord> records = new ArrayList<LogRecord>();

		public synchronized void add(LogRecord record) {
			int index = getRowCount();
			records.add(record);
			fireTableRowsInserted(index, index);
		}

		@Override
		public int getColumnCount() {
			return LogTableColumn.values().length;
		}

		@Override
		public int getRowCount() {
			return records.size();
		}

		private LogTableColumn getColumn(int col) {
			return LogTableColumn.values()[col];
		}

		@Override
		public String getColumnName(int col) {
			return getColumn(col).toString();
		}

		public int getColumnIndex(LogTableColumn column) {
			return column.ordinal();
		}

		@Override
		public Object getValueAt(int row, int col) {
			if (row >= getRowCount())
				return "";
			return getColumn(col).getValue(records.get(row));
		}

		@Override
		public Class<?> getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}

		@Override
		public void setValueAt(Object value, int row, int col) {

		}
	}

}
