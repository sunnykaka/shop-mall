package com.kariqu.securitysystem.specifaction;

/**
 * Responsibility:OR specification, used to create a new specifcation that is the OR of two other specifications.
 * @param <T>
 */
public class OrSpecification<T> extends AbstractSpecification<T> {

  /**
   * the first factor of [or predicate].
   */
  private Specification<T> spec1;

  /**
   * the second factor of [or predicate].
   */
  private Specification<T> spec2;

  /**
   * Create a new OR specification based on two other specification. Constructor.
   * @param spec1 first specification
   * @param spec2 second specification
   */
  public OrSpecification(final Specification<T> spec1, final Specification<T> spec2) {
    this.spec1 = spec1;
    this.spec2 = spec2;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isSatisfiedBy(final T t) {
    return spec1.isSatisfiedBy(t) || spec2.isSatisfiedBy(t);
  }
}
