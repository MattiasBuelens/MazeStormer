package mazestormer.ui;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.Beans;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import mazestormer.controller.ILogController;

public class LogPanel extends ViewPanel {

	private static final long serialVersionUID = 1L;

	private final ILogController controller;

	private LogTableModel tableModel;

	public LogPanel(ILogController controller) {
		this.controller = controller;

		setBorder(new TitledBorder(null, "Log", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		setLayout(new BorderLayout(0, 0));

		final JTable table = new JTable();
		tableModel = new LogTableModel();
		table.setModel(tableModel);
		add(new JScrollPane(table));

		// Limit size of first column
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		TableColumn levelColumn = table.getColumnModel().getColumn(0);
		levelColumn.setPreferredWidth(100);
		levelColumn.setMaxWidth(100);

		// Scroll to last row when inserting new records
		table.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				table.scrollRectToVisible(table.getCellRect(
						table.getRowCount() - 1, 0, true));
			}
		});

		if (!Beans.isDesignTime())
			registerController();
	}

	private void registerController() {
		registerEventBus(controller.getEventBus());

		controller.addLogHandler(new LogHandler());
	}

	private class LogHandler extends Handler {

		@Override
		public void publish(LogRecord record) {
			if (isLoggable(record)) {
				tableModel.add(record);
			}
		}

		@Override
		public void flush() {

		}

		@Override
		public void close() throws SecurityException {

		}
	}

	private enum LogTableColumn {
		Level {
			@Override
			public Object getValue(LogRecord record) {
				return record.getLevel().getLocalizedName();
			}
		},
		Message {
			@Override
			public Object getValue(LogRecord record) {
				return record.getMessage();
			}
		};

		public abstract Object getValue(LogRecord record);
	};

	private class LogTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		private List<LogRecord> records = new ArrayList<LogRecord>();

		public void add(LogRecord record) {
			int index = getRowCount();
			records.add(record);
			fireTableRowsInserted(index, index);
		}

		public void clear() {
			records.clear();
			fireTableDataChanged();
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

		@Override
		public Object getValueAt(int row, int col) {
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
