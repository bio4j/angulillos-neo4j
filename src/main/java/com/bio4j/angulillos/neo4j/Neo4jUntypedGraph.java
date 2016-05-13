package com.bio4j.angulillos.neo4j;

import java.util.stream.Stream;
import java.util.Iterator;
import java.util.Optional;

import com.bio4j.angulillos.*;
import static com.bio4j.angulillos.conversions.*;

// Neo4j
import org.neo4j.graphdb.*;
import static org.neo4j.graphdb.Direction.*;

public class Neo4jUntypedGraph
implements
  UntypedGraph<Node, Relationship>
{
  private final GraphDatabaseService neo4jGraph;
  public  final GraphDatabaseService neo4jGraph() { return this.neo4jGraph; }

  public Neo4jUntypedGraph(GraphDatabaseService neo4jGraph) { this.neo4jGraph = neo4jGraph; }


  @Override
  public void commit() { throw new UnsupportedOperationException(); }

  @Override
  public void shutdown() { neo4jGraph().shutdown(); }

  @Override
  public void rollback() { throw new UnsupportedOperationException(); }

  private RelationshipType relType(AnyEdgeType edgeType) {
    return DynamicRelationshipType.withName(edgeType._label());
  }

  @Override
  public Relationship addEdge(Node from, AnyEdgeType edgeType, Node to) {

    return from.createRelationshipTo(to, relType(edgeType));
  }

  @Override
  public Node addVertex(AnyVertexType vertexType) {

    return neo4jGraph().createNode(DynamicLabel.label(vertexType._label()));
  }


  //////////////////////////////////////////////////////////////////////////////////

  @Override
  public <V> V getPropertyV(Node vertex, AnyProperty property) {

    @SuppressWarnings("unchecked")
    V value = (V) vertex.getProperty(property._label());
    return value;
  }

  @Override
  public <V> Node setPropertyV(Node vertex, AnyProperty property, V value) {

    vertex.setProperty(property._label(), value);
    return vertex;
  }

  @Override
  public <V> V getPropertyE(Relationship edge, AnyProperty property) {

    @SuppressWarnings("unchecked")
    V value = (V) edge.getProperty(property._label());
    return value;
  }

  @Override
  public <V> Relationship setPropertyE(Relationship edge, AnyProperty property, V value) {

    edge.setProperty(property._label(), value);
    return edge;
  }

  @Override
  public Node source(Relationship edge) {

    return edge.getStartNode();
  }

  @Override
  public Node target(Relationship edge) {

    return edge.getEndNode();
  }

  @Override
  public Stream<Relationship> outE(Node vertex, AnyEdgeType edgeType) {

    return stream( vertex.getRelationships(relType(edgeType), OUTGOING) );
  }

  @Override
  public Stream<Node> outV(Node vertex, AnyEdgeType edgeType) {

    return stream(
      vertex.getRelationships(relType(edgeType), OUTGOING)
    ).map( Relationship::getEndNode );
  }

  @Override
  public Stream<Relationship> inE(Node vertex, AnyEdgeType edgeType) {

    return stream( vertex.getRelationships(relType(edgeType), INCOMING) );
  }

  @Override
  public Stream<Node> inV(Node vertex, AnyEdgeType edgeType) {

    return stream (
      vertex.getRelationships(relType(edgeType), INCOMING)
    ).map( Relationship::getStartNode );
  }
}
