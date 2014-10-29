package com.bio4j.angulillos.neo4j;

import java.util.stream.Stream;
import java.util.Iterator;
import java.util.Optional;

import com.bio4j.angulillos.*;
import static com.bio4j.angulillos.conversions.*;

// Neo4j
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

import static org.neo4j.graphdb.Direction.*;

import org.neo4j.graphdb.schema.ConstraintDefinition;
import org.neo4j.graphdb.schema.ConstraintCreator;

public interface Neo4jUntypedGraph extends UntypedGraph <
  // vertex and vertex type
  org.neo4j.graphdb.Node, org.neo4j.graphdb.Label,
  // edge and edge type
  org.neo4j.graphdb.Relationship, org.neo4j.graphdb.RelationshipType
> 
{

  org.neo4j.graphdb.GraphDatabaseService neo4jGraph();

  default void commit() {  }

  default void shutdown() { neo4jGraph().shutdown(); }

  @Override
  default Relationship addEdge(Node from, RelationshipType edgeType, Node to) {

    return from.createRelationshipTo(to, edgeType);
  }

  @Override
  default Node addVertex(Label type) {  return neo4jGraph().createNode(type);  }

  @Override
  default <V> V getPropertyV(Node vertex, String property) {  

    @SuppressWarnings("unchecked")
    V value = (V) vertex.getProperty( property ); 
    return value;
  }

  @Override
  default <V> void setPropertyV(Node vertex, String property, V value) {

    vertex.setProperty( property, value ); 
  }

  @Override
  default <V> V getPropertyE(Relationship edge, String property) {

    @SuppressWarnings("unchecked")
    V value = (V) edge.getProperty( property ); 
    return value;
  }

  @Override
  default <V> void setPropertyE(Relationship edge, String property, V value) {

    edge.setProperty( property, value );
  }

  @Override
  default Node source(Relationship edge) {

    return edge.getStartNode();
  }

  @Override
  default Node target(Relationship edge) {

    return edge.getEndNode();    
  }

  @Override
  default Optional<Stream<Relationship>> out(Node vertex, RelationshipType edgeType) {

    return Optional.ofNullable( stream( vertex.getRelationships( edgeType, OUTGOING ) ) );
  }

  @Override
  default Optional<Stream<Node>> outV(Node vertex, RelationshipType edgeType) {

    return Optional.ofNullable ( 
      stream ( 
        vertex.getRelationships( edgeType, OUTGOING ) 
      )
      .map( Relationship::getEndNode ) 
    );
  }

  @Override
  default Optional<Stream<Relationship>> in(Node vertex, RelationshipType edgeType) {

    return Optional.ofNullable( stream( vertex.getRelationships( edgeType, INCOMING ) ) );
  }

  @Override
  default Optional<Stream<Node>> inV(Node vertex, RelationshipType edgeType) {

    return Optional.ofNullable ( 
      stream ( 
        vertex.getRelationships( edgeType, INCOMING ) 
      )
      .map( Relationship::getStartNode ) 
    );
  }

  default <
    N extends TypedVertex<N,NT,G,I,Node,Label,Relationship,RelationshipType>,
    NT extends TypedVertex.Type<N,NT,G,I,Node,Label,Relationship,RelationshipType>,
    P extends Property<N,NT,P,V,G,I,Node,Label,Relationship,RelationshipType>, V,
    G extends TypedGraph<G,I,Node,Label,Relationship,RelationshipType>,
    I extends Neo4jUntypedGraph
  > ConstraintCreator uniqueConstraintFor(P property) {

    return neo4jGraph().schema().constraintFor(property.elementType().raw())
      .assertPropertyIsUnique(property.name());
  }

  default <
    N extends TypedVertex<N,NT,G,I,Node,Label,Relationship,RelationshipType>,
    NT extends TypedVertex.Type<N,NT,G,I,Node,Label,Relationship,RelationshipType>,
    P extends Property<N,NT,P,V,G,I,Node,Label,Relationship,RelationshipType>, V,
    G extends TypedGraph<G,I,Node,Label,Relationship,RelationshipType>,
    I extends Neo4jUntypedGraph
  > ConstraintDefinition createOrGetUniqueConstraintFor(P property) {

    return Optional.ofNullable ( 
      neo4jGraph().schema()
      .getConstraints( property.elementType().raw() )
      .iterator().next()
    ).orElseGet(
      () -> uniqueConstraintFor(property).create()
    );
  }

}