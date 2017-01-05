import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.melastmohican.osquery.BasePlugin;
import net.melastmohican.osquery.PluginManager;
import net.melastmohican.osquery.TablePlugin;
import net.melastmohican.osquery.TablePlugin.TableColumn;
import net.melastmohican.osquery.TablePluginTest.SimpleTablePlugin;

public class MyTablePlugin extends TablePlugin {

	@Override
	public List<TableColumn> columns() {
		return Arrays.asList(new TableColumn("foo", "TEXT"), new TableColumn("baz", "TEXT"));
	}

	@Override
	public String name() {
		return "mytable";
	}

	@Override
	public List<Map<String, String>> generate() {
		List<Map<String, String>> result = Arrays.<Map<String, String>>asList(new HashMap<String, String>() {
			{
				put("foo", "bar");
				put("baz", "baz");
			}
		}, new HashMap<String, String>() {
			{
				put("foo", "bar");
				put("baz", "baz");
			}
		});
		return result;
	}

	public static void main(String[] args) {
		BasePlugin plugin = new MyTablePlugin();
		PluginManager pm = PluginManager.getInstance();
		pm.addPlugin(plugin);
		pm.startExtension("MyTablePlugin","0.0.1","2.2.1","2.2.1");
	}

}
