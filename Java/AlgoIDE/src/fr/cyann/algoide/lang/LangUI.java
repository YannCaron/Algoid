/* 
 * Copyright (C) 2014 cyann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.cyann.algoide.lang;

import java.awt.Component;
import java.awt.Container;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

/**
 *
 * @author caronyn
 */
public class LangUI {

	// constant
	public static final String STRINGS_FILENAME = "properties/Strings";
	public static final String DEFAULT_STRING = "Missing locale resource [%s] !";

	// nested classes
	private abstract class LabelAccessor<T extends Component> {

		public abstract void apply(T component);
	}
	// attribut
	private static LangUI singleton = null;
	// attributs
	ResourceBundle bundle;
	private Map<Class<? extends Component>, LabelAccessor> map;

	// constructor
	private LangUI() {
		loadStringsBundle(Locale.getDefault());

		map = new HashMap<Class<? extends Component>, LabelAccessor>();
		map.put(JLabel.class, new LabelAccessor<JLabel>() {

			@Override
			public void apply(JLabel component) {
				if (component.getName() != null) {
					component.setText(getString(component.getName(), component.getText()));
				}
			}
		});

		map.put(JCheckBox.class, new LabelAccessor<JCheckBox>() {

			@Override
			public void apply(JCheckBox component) {
				if (component.getName() != null) {
					component.setText(getString(component.getName(), component.getText()));
				}
			}
		});

		map.put(JMenu.class, new LabelAccessor<JMenu>() {

			@Override
			public void apply(JMenu component) {
				if (component.getName() != null) {
					component.setText(getString(component.getName(), component.getText()));
				}

				JMenu menu = (JMenu) component;
				for (int i = 0; i < menu.getItemCount(); i++) {
					applyLocale(menu.getItem(i));
				}

			}
		});

		map.put(JMenuItem.class, new LabelAccessor<JMenuItem>() {

			@Override
			public void apply(JMenuItem component) {
				if (component.getName() != null) {
					component.setText(getString(component.getName(), component.getText()));
				}
			}
		});

		map.put(JTabbedPane.class, new LabelAccessor<JTabbedPane>() {

			@Override
			public void apply(JTabbedPane component) {
				int size = component.getTabCount();
				for (int i = 0; i < size; i++) {
					String key = "tp_" + component.getTitleAt(i).replace(' ', '_');
					component.setTitleAt(i, getString(key, component.getTitleAt(i)));
				}
			}
		});

		map.put(Container.class, new LabelAccessor<Container>() {

			@Override
			public void apply(Container component) {
				for (Component c : component.getComponents()) {
					applyLocale(c);
				}
			}
		});
	}

	public static LangUI getInstance() {
		if (singleton == null) {
			singleton = new LangUI();
		}

		return singleton;
	}

	// function
	private void loadStringsBundle(Locale locale) {
		try {
			bundle = ResourceBundle.getBundle(STRINGS_FILENAME, locale);
		} catch (MissingResourceException ex) {
			bundle = ResourceBundle.getBundle(STRINGS_FILENAME, Locale.ENGLISH);
		}
	}

	// method
	public String getString(String key, String defaultValue) {
		try {
			return bundle.getString(key);
		} catch (MissingResourceException ex) {
			return defaultValue;
		}
	}

	public String getString(String key) {
		try {
			return bundle.getString(key);
		} catch (MissingResourceException ex) {
			return String.format(DEFAULT_STRING, key);
		}
	}

	public void applyString(Component component) {
		if (component != null) {

			/*if (component.getName() != null) {
				System.out.println("Set label for " + component.getName());
			}*/

			LabelAccessor accessor = map.get(component.getClass());

			if (accessor != null) {
				accessor.apply(component);
			}
		}
	}

	public void applyLocale(Component component) {

		applyString(component);

		if (component instanceof Container) {
			map.get(Container.class).apply(component);
		}
	}
}
