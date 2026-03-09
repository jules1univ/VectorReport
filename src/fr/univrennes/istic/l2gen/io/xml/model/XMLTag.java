package fr.univrennes.istic.l2gen.io.xml.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class XMLTag {

  protected final String name;
  protected Optional<String> content;

  private final Map<String, XMLAttribute> attributes = new LinkedHashMap<>();
  protected final List<XMLTag> children = new ArrayList<>();

  public XMLTag(String name) {
    this(name, null);
  }

  public XMLTag(String name, String content) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("Tag name cannot be null or empty");
    }
    this.name = name;
    this.content = Optional.ofNullable(content);
  }

  public final String getTagName() {
    return name;
  }

  public final Optional<String> getTextContent() {
    return content;
  }

  public final boolean hasTextContent() {
    return content.filter(s -> !s.isEmpty()).isPresent();
  }

  public final void setTextContent(String content) {
    this.content = Optional.ofNullable(content);
  }

  public final boolean hasAttribute(String name) {
    return attributes.containsKey(name);
  }

  public final XMLAttribute getAttribute(String name) {
    return attributes.get(name);
  }

  public final void addAttribute(XMLAttribute attr) {
    attributes.put(attr.name(), Objects.requireNonNull(attr));
  }

  public final void addAttribute(String name, Object value) {
    attributes.put(name, new XMLAttribute(name, value.toString()));
  }

  public final void removeAttribute(String name) {
    attributes.remove(name);
  }

  public final int getAttributeCount() {
    return attributes.size();
  }

  public final Stream<XMLAttribute> attributeStream() {
    return attributes.values().stream();
  }

  public void appendChild(XMLTag child) {
    if (child == null) {
      throw new IllegalArgumentException("Child tag cannot be null");
    }
    children.add(child);
  }

  public void insertChildAt(int index, XMLTag child) {
    if (child == null) {
      throw new IllegalArgumentException("Child tag cannot be null");
    }
    if (index < 0 || index > children.size()) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + children.size());
    }
    this.children.add(index, child);
  }

  public boolean removeChild(XMLTag child) {
    return this.children.remove(child);
  }

  public boolean hasChildren() {
    return !this.children.isEmpty();
  }

  public int getChildrenCount() {
    return this.children.size();
  }

  public List<XMLTag> getChildren() {
    return this.children;
  }

  public Optional<XMLTag> getChildAt(int index) {
    if (index < 0 || index >= children.size()) {
      return Optional.empty();
    }
    return Optional.of(this.children.get(index));
  }

  public Stream<XMLTag> childStream() {
    return this.children.stream();
  }

  public Optional<XMLTag> getFirstChildByName(String tagName) {
    return this.children.stream()
        .filter(c -> c.name.equals(tagName))
        .findFirst();
  }

  public Optional<XMLTag> getFirstChild() {
    return this.children.isEmpty() ? Optional.empty() : Optional.of(this.children.get(0));
  }

  public Optional<XMLTag> findFirst(String tagName) {
    return childrenStream()
        .filter(c -> c.name.equals(tagName))
        .findFirst();
  }

  public List<XMLTag> findAll(String tagName) {
    return childrenStream()
        .filter(c -> c.name.equals(tagName))
        .collect(Collectors.toList());
  }

  public Stream<XMLTag> childrenStream() {
    return children.stream().flatMap(child -> Stream.concat(Stream.of(child), child.childrenStream()));
  }

  @Override
  public final String toString() {
    return this.toFormattedString(0, false, 0).toString();
  }

  public final StringBuilder toFormattedString(int depth, boolean pretty, int indentSize) {
    return toFormattedString(new StringBuilder(), depth, pretty, indentSize);
  }

  public final StringBuilder toFormattedString(StringBuilder sb, int depth, boolean pretty, int indentSize) {
    String pad = pretty ? " ".repeat(depth * indentSize) : "";
    String nl = pretty ? "\n" : "";

    sb.append(pad).append("<").append(name);
    for (XMLAttribute attr : attributes.values()) {
      sb.append(" ").append(attr.toString());
    }

    boolean hasContent = content.isPresent();
    boolean hasChildren = !children.isEmpty();

    if (!hasContent && !hasChildren) {
      sb.append("/>");
      return sb;
    }

    sb.append(">");

    if (hasContent) {
      sb.append(escape(content.get()));
    }

    if (hasChildren) {
      sb.append(nl);
      for (XMLTag child : children) {
        child.toFormattedString(sb, depth + 1, pretty, indentSize);
        sb.append(nl);
      }
      sb.append(pad);
    }

    sb.append("</").append(name).append(">");
    return sb;
  }

  public static String escape(String text) {
    return text
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;");
  }

  public static String unescape(String text) {
    return text
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&apos;", "'")
        .replace("&amp;", "&");
  }
}