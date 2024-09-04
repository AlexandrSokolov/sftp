package com.example.sftp;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.*;
import java.util.stream.*;

abstract class AbstractSftpStream<T> implements Stream<T> {

  private final Stream<T> originalStream;

  public AbstractSftpStream(Stream<T> originalStream) {
    this.originalStream = originalStream;
  }

  @Override
  public Stream<T> filter(Predicate<? super T> predicate) {
    return originalStream.filter(predicate);
  }

  @Override
  public <R> Stream<R> map(Function<? super T, ? extends R> function) {
    return originalStream.map(function);
  }

  @Override
  public IntStream mapToInt(ToIntFunction<? super T> toIntFunction) {
    return originalStream.mapToInt(toIntFunction);
  }

  @Override
  public LongStream mapToLong(ToLongFunction<? super T> toLongFunction) {
    return originalStream.mapToLong(toLongFunction);
  }

  @Override
  public DoubleStream mapToDouble(ToDoubleFunction<? super T> toDoubleFunction) {
    return originalStream.mapToDouble(toDoubleFunction);
  }

  @Override
  public <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> function) {
    return originalStream.flatMap(function);
  }

  @Override
  public IntStream flatMapToInt(Function<? super T, ? extends IntStream> function) {
    return originalStream.flatMapToInt(function);
  }

  @Override
  public LongStream flatMapToLong(Function<? super T, ? extends LongStream> function) {
    return originalStream.flatMapToLong(function);
  }

  @Override
  public DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> function) {
    return originalStream.flatMapToDouble(function);
  }

  @Override
  public Stream<T> distinct() {
    return originalStream.distinct();
  }

  @Override
  public Stream<T> sorted() {
    return originalStream.sorted();
  }

  @Override
  public Stream<T> sorted(Comparator<? super T> comparator) {
    return originalStream.sorted(comparator);
  }

  @Override
  public Stream<T> peek(Consumer<? super T> consumer) {
    return originalStream.peek(consumer);
  }

  @Override
  public Stream<T> limit(long l) {
    return originalStream.limit(l);
  }

  @Override
  public Stream<T> skip(long l) {
    return originalStream.skip(l);
  }

  @Override
  public void forEach(Consumer<? super T> consumer) {
    originalStream.forEach(consumer);
  }

  @Override
  public void forEachOrdered(Consumer<? super T> consumer) {
    originalStream.forEachOrdered(consumer);
  }

  @Override
  public Object[] toArray() {
    return originalStream.toArray();
  }

  @Override
  public <A> A[] toArray(IntFunction<A[]> intFunction) {
    return originalStream.toArray(intFunction);
  }

  @Override
  public T reduce(T t, BinaryOperator<T> binaryOperator) {
    return originalStream.reduce(t, binaryOperator);
  }

  @Override
  public Optional<T> reduce(BinaryOperator<T> binaryOperator) {
    return originalStream.reduce(binaryOperator);
  }

  @Override
  public <U> U reduce(U u, BiFunction<U, ? super T, U> biFunction, BinaryOperator<U> binaryOperator) {
    return originalStream.reduce(u, biFunction, binaryOperator);
  }

  @Override
  public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> biConsumer, BiConsumer<R, R> biConsumer1) {
    return originalStream.collect(supplier, biConsumer, biConsumer1);
  }

  @Override
  public <R, A> R collect(Collector<? super T, A, R> collector) {
    return originalStream.collect(collector);
  }

  @Override
  public Optional<T> min(Comparator<? super T> comparator) {
    return originalStream.min(comparator);
  }

  @Override
  public Optional<T> max(Comparator<? super T> comparator) {
    return originalStream.max(comparator);
  }

  @Override
  public long count() {
    return originalStream.count();
  }

  @Override
  public boolean anyMatch(Predicate<? super T> predicate) {
    return originalStream.anyMatch(predicate);
  }

  @Override
  public boolean allMatch(Predicate<? super T> predicate) {
    return originalStream.allMatch(predicate);
  }

  @Override
  public boolean noneMatch(Predicate<? super T> predicate) {
    return originalStream.noneMatch(predicate);
  }

  @Override
  public Optional<T> findFirst() {
    return originalStream.findFirst();
  }

  @Override
  public Optional<T> findAny() {
    return originalStream.findAny();
  }

  @Override
  public Iterator<T> iterator() {
    return originalStream.iterator();
  }

  @Override
  public Spliterator<T> spliterator() {
    return originalStream.spliterator();
  }

  @Override
  public boolean isParallel() {
    return originalStream.isParallel();
  }

  @Override
  public Stream<T> sequential() {
    return originalStream.sequential();
  }

  @Override
  public Stream<T> parallel() {
    return originalStream.parallel();
  }

  public Stream<T> unordered() {
    return originalStream.unordered();
  }
}
