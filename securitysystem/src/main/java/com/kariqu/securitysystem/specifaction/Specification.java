package com.kariqu.securitysystem.specifaction;

/**
 * Responsibility: The specification interface that used for checking parameter if valid.
 * A specification is a predicate that determines if an object does or does not satisfy some criteria.
 * This pattern produced by Martin Fowler and Evans
 * @param <T>
 */
public interface Specification<T> {

  /**
   * Check if {@code t} is satisfied by the specification.
   * @param t the object to be check with specification
   * @return if satisfied
   */
  boolean isSatisfiedBy(T t);

  /**
   * Create a new specification that is the AND operation of {@code this} specification and another specification.
   * @param specification [and] target specification
   * @return also one specification
   */
  Specification<T> and(Specification<T> specification);

  /**
   * Create a new specification that is the OR operation of {@code this} specification and another specification.
   * @param specification [or] target specification
   * @return also one specification
   */
  Specification<T> or(Specification<T> specification);

  /**
   * Create a new specification that is the NOT operation of {@code this} specification.
   * @param specification [not] target specification
   * @return also one specification
   */
  Specification<T> not(Specification<T> specification);
}
