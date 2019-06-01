package test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class TestBean2 {

	private List<ColumnModel> columns;

	private Map<String, List<Integer>> results;
	private String columnName;

	public TestBean2(){
		results = new LinkedHashMap<String, List<Integer>>();
	}

	public void gameFinished(String id, Integer count){
		System.out.println("TestBean2.gameFinished :: id=" + id + ", count=" + count);

		List<Integer> lst = null;
		if( results.containsKey(id) ){
			lst = results.get(id);
		} else {
			lst = new ArrayList<Integer>();
		}
		lst.add(count);
		results.put(id, lst);

		createDynamicColumns();
	}

	public Integer[][] getArray(){

		if(results==null || results.size()==0){
			return null;
		}

		int colnum = results.keySet().size();
		int rownum = 0;
		for(List<Integer> lst : results.values()){
			int cnt = lst.size();
			if( cnt > rownum )
				rownum = cnt;
		}
		System.out.println("TestBean2.getArray :: rownum=" + rownum + ", colnum=" + colnum);

		Integer[][] ret = new Integer[rownum][colnum];

		int c = 0;
		for(Entry<String, List<Integer>> e : results.entrySet()){
			for(int r = 0; r<e.getValue().size(); r++){
				ret[r][c] = e.getValue().get(r);
			}
			c++;
		}

		return ret;
	}

	private void createDynamicColumns() {
        columns = new ArrayList<ColumnModel>();
        if( results==null || results.size()==0 )
        	return;

        for(String k : results.keySet()){
        	columns.add(new ColumnModel(k, k));
        }
    }

	public List<ColumnModel> getColumns() {
        return columns;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

	static public class ColumnModel implements Serializable {

        private String header;
        private String property;

        public ColumnModel(String header, String property) {
            this.header = header;
            this.property = property;
        }

        public String getHeader() {
            return header;
        }

        public String getProperty() {
            return property;
        }
    }

}