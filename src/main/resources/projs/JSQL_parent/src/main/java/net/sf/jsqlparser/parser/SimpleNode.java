
/* JavaCCOptions:MULTI=false,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package net.sf.jsqlparser.parser;

public
class SimpleNode implements Node {

  protected Node parent;
  protected Node[] children;
  protected int id;
  protected Object value;
  protected CCJSqlParser parser;
  protected Token firstToken;
  protected Token lastToken;

  public SimpleNode(int i) {
    id = i;
  }

  public SimpleNode(CCJSqlParser p, int i) {
    this(i);
    parser = p;
  }

  public void jjtOpen() {
  }

  public void jjtClose() {
  }

  public void jjtSetParent(Node n) { parent = n; }
  public Node jjtGetParent() { return parent; }

  public void jjtAddChild(Node n, int i) {
    if (children == null) {
      children = new Node[i + 1];
    } else if (i >= children.length) {
      Node c[] = new Node[i + 1];
      System.arraycopy(children, 0, c, 0, children.length);
      children = c;
    }
    children[i] = n;
  }

  public Node jjtGetChild(int i) {
    return children[i];
  }

  public int jjtGetNumChildren() {
    return (children == null) ? 0 : children.length;
  }

  public void jjtSetValue(Object value) { this.value = value; }
  public Object jjtGetValue() { return value; }

  public Token jjtGetFirstToken() { return firstToken; }
  public void jjtSetFirstToken(Token token) { this.firstToken = token; }
  public Token jjtGetLastToken() { return lastToken; }
  public void jjtSetLastToken(Token token) { this.lastToken = token; }

  /** Accept the visitor. **/
  public Object jjtAccept(CCJSqlParserVisitor visitor, Object data)
{
    return visitor.visit(this, data);
  }

  /** Accept the visitor. **/
  public Object childrenAccept(CCJSqlParserVisitor visitor, Object data)
{
    if (children != null) {
      for (int i = 0; i < children.length; ++i) {
        children[i].jjtAccept(visitor, data);
      }
    }
    return data;
  }

  /* You can override these two methods in subclasses of SimpleNode to
     customize the way the node appears when the tree is dumped.  If
     your output uses more than one line you should override
     toString(String), otherwise overriding toString() is probably all
     you need to do. */

  public String toString() {
    return CCJSqlParserTreeConstants.jjtNodeName[id];
  }
  public String toString(String prefix) { return prefix + toString(); }

  /* Override this method if you want to customize how the node dumps
     out its children. */

  public void dump(String prefix) {
    System.out.println(toString(prefix));
    if (children != null) {
      for (int i = 0; i < children.length; ++i) {
        SimpleNode n = (SimpleNode)children[i];
        if (n != null) {
          n.dump(prefix + " ");
        }
      }
    }
  }

  public int getId() {
    return id;
  }
}

/* JavaCC - OriginalChecksum=283a34f60b6dd39cf9b7e6055e034f11 (do not edit this line) */
