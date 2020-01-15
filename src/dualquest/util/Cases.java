package dualquest.util;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cases implements ConfigurationSerializable {

	private ChatColor color;
	private String nominative;
	private String genitive;
	private String accusative;
	private String dative;
	private String instrumental;
	private String prepositional;

	public Cases(ChatColor defaultColor, String nominative, String genitive, String dative, String accusative, String instrumental, String prepositional) {
		this(defaultColor + nominative, defaultColor + genitive, defaultColor + dative, defaultColor + accusative, defaultColor + instrumental, defaultColor + prepositional);
	}

	public Cases(String nominative, String genitive, String dative, String accusative, String instrumental, String prepositional) {
		this.nominative = nominative;
		this.genitive = genitive;
		this.accusative = accusative;
		this.dative = dative;
		this.instrumental = instrumental;
		this.prepositional = prepositional;
	}

	public static List<String> getCaseNames() {
		Case[] cases = Case.values();
		return Lists.newArrayList("nominative", "genitive", "dative", "accusative", "instrumental", "prepositional");
	}

	public static Cases deserialize(Map<String, Object> data) {
		return new Cases((String) data.get("nominative"), (String) data.get("genitive"), (String) data.get("dative"), (String) data.get("accusative"),
				(String) data.get("instrumental"), (String) data.get("prepositional"));
	}

	public List<String> getCases() {
		return Lists.<String>newArrayList(nominative, genitive, dative, accusative, instrumental, prepositional);
	}

	public String getByCase(Case c) {
		switch(c) {
		case NOMINATIVE:
			return getNominative();
		case GENITIVE:
			return getGenitive();
		case DATIVE:
			return getDative();
		case ACCUSATIVE:
			return getAccusative();
		case INSTRUMENTAL:
			return getInstrumental();
		case PREPOSITIONAL:
			return getPrepositional();
		default:
			return null;
		}
	}

	public String getNominative() {
		return nominative;
	}

	public void setNominative(String nominative) {
		this.nominative = nominative;
	}

	public String getGenitive() {
		return genitive;
	}

	public void setGenitive(String genitive) {
		this.genitive = genitive;
	}

	public String getAccusative() {
		return accusative;
	}

	public void setAccusative(String accusative) {
		this.accusative = accusative;
	}

	public String getDative() {
		return dative;
	}

	public void setDative(String dative) {
		this.dative = dative;
	}

	public String getInstrumental() {
		return instrumental;
	}

	public void setInstrumental(String instrumental) {
		this.instrumental = instrumental;
	}

	public String getPrepositional() {
		return prepositional;
	}

	public void setPrepositional(String prepositional) {
		this.prepositional = prepositional;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		for(int i = 0; i < getCases().size(); i++) {
			map.put(getCaseNames().get(i), getCases().get(i));
		}
		return map;
	}

	public enum Case {

		NOMINATIVE,
		GENITIVE,
		DATIVE,
		ACCUSATIVE,
		INSTRUMENTAL,
		PREPOSITIONAL;

		public static Case byOrdinal(int n) {
			for(Case c : values()) {
				if(c.ordinal() == n) return c;
			}
			return null;
		}

	}

}
