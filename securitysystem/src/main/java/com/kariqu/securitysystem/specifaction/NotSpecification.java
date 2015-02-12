package com.kariqu.securitysystem.specifaction;

/**
 * Responsibility:NOT decorator, used to create a new specifcation that is the inverse (NOT) of the given spec.
 * @param <T>
 */
public class NotSpecification<T> extends AbstractSpecification<T> {

  /**
   * the factor for the [not predicate].
   */
  private Specification<T> spec1;

  /**
   * Create a new NOT specification based on another specification. Constructor.
   * @param spec1 the target specification for [Not predicate]
   */
  public NotSpecification(final Specification<T> spec1) {
    this.spec1 = spec1;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isSatisfiedBy(final T t) {
    return !spec1.isSatisfiedBy(t);
  }
}
