package yel1ow.messenger.client.GUI.table_models;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class DefaultTableModel extends AbstractTableModel {

    private final int columnCount = 1;
    private final ArrayList<String> dataArrayList = new ArrayList<>();

    public DefaultTableModel() {
//        for(int i = 0; i < messagesArrayList.size(); i++) {
//            messagesArrayList.add(new String[getColumnCount()]);
//        }
    }

    public void addData(String message) {
        dataArrayList.add(message);
    }

    public void removeData(String message) {
        dataArrayList.remove(message);
    }

    @Override
    public int getRowCount() {
        return dataArrayList.size();
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public String getColumnName(int columnIndex) {
        if(columnIndex == 0) {
            return "dialog";
        }
        return "";
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return dataArrayList.get(rowIndex);
    }
}
