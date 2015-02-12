package com.kariqu.securitysystem.specifaction;

/**
 * Responsibility:AND specification, used to create a new specification that is the AND of two other specifications.
 * @param <T>
 */
public class AndSpecification<T> extends AbstractSpecification<T> {

  /**
   * the first factor of [and predicate].
   */
  private Specification<T> spec1;

  /**
   * the second factor of [and predicate].
   */
  private Specification<T> spec2;

  /**
   * Create a new AND specification based on two other specification. Constructor.
   * @param spec1 the first specification for [AND] compute.
   * @param spec2 the second specification for [AND] compute.
   */
  public AndSpecification(final Specification<T> spec1, final Specification<T> spec2) {
    this.spec1 = spec1;
    this.spec2 = spec2;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isSatisfiedBy(final T t) {
    return spec1.isSatisfiedBy(t) && spec2.isSatisfiedBy(t);
  }
}
