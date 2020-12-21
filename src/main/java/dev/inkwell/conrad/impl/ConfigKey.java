package dev.inkwell.conrad.impl;

import net.minecraft.util.InvalidIdentifierException;

import java.util.Arrays;
import java.util.Objects;

public class ConfigKey {
	private final String namespace;
	private final String[] path;

	public ConfigKey(String namespace, String... path) {
		this.namespace = namespace;
		this.path = path;

		if (!isNamespaceValid(namespace)) {
			throw new InvalidIdentifierException("Non [a-z0-9_-] character in namespace of location: " + this.toString());
		} else if (!isPathValid(this.path)) {
			throw new InvalidIdentifierException("Non [a-z0-9_-] character in path of location: " + this.toString());
		}
	}

	public ConfigKey(ConfigKey key, String... path) {
		this.namespace = key.namespace;
		this.path = Arrays.copyOf(key.path, key.path.length + path.length);
		System.arraycopy(path, 0, this.path, key.path.length, path.length);

		if (!isPathValid(this.path)) {
			throw new InvalidIdentifierException("Non [a-z0-9_-] character in path of location: " + this.toString());
		}
	}

	public String getNamespace() {
		return namespace;
	}

	public String[] getPath() {
		return path;
	}

	public String toString() {
		return namespace + ':' + String.join("/", path);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ConfigKey configKey = (ConfigKey) o;
		return namespace.equals(configKey.namespace) &&
				Arrays.equals(path, configKey.path);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(namespace);
		result = 31 * result + Arrays.hashCode(path);
		return result;
	}

	private static boolean isPathValid(String[] path) {
		for (String string : path) {
			for (int i = 0; i < string.length(); ++i) {
				if (!isPathCharacterValid(string.charAt(i))) {
					return false;
				}
			}
		}

		return true;
	}

	private static boolean isNamespaceValid(String namespace) {
		for(int i = 0; i < namespace.length(); ++i) {
			if (!isNamespaceCharacterValid(namespace.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	public static boolean isPathCharacterValid(char c) {
		return c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9';
	}

	private static boolean isNamespaceCharacterValid(char c) {
		return c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9';
	}
}
