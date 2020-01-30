package com.link_intersystems.maven.mojo.parameter;

public interface PropertyEditorComponent<T> {

	public T parseProperty(String property) throws PropertyParseException;
}
