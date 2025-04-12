package mylie.engine.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Vault extends Component {
	private final List<Object> vaultItems;
	public Vault(ComponentManager manager) {
		super(manager);
		this.vaultItems = new CopyOnWriteArrayList<>();
	}

	public <T> T item(Class<T> type) {
		for (Object vaultItem : vaultItems) {
			if (type.isAssignableFrom(vaultItem.getClass())) {
				return type.cast(vaultItem);
			}
		}
		return null;
	}

	public void addItem(Object item) {
		vaultItems.add(item);
	}

	public void removeItem(Object item) {
		vaultItems.remove(item);
	}
}
