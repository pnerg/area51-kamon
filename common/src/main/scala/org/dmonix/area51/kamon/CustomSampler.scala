package org.dmonix.area51.kamon

import kamon.Kamon
import kamon.trace.{RandomSampler, Sampler, Trace}


/**
  * Custom decision point if the span for a particular URI/path is to be sampled, i.e. reported to Zipkin. 
  * @author Peter Nerg
  */
class CustomSampler extends Sampler with PathFilter {
  
  // uses the random sampler in case we pass the URI filtering
  // configured using the same means as if the sampler would be created by Kamon itself
  // https://github.com/kamon-io/Kamon/blob/master/kamon-core/src/main/scala/kamon/trace/Tracer.scala#L363
  private val sampler = RandomSampler(Kamon.config().getDouble("kamon.trace.random-sampler.probability"))

  /**
    * Decides if the operation/path is to be sampled.
    * First matches the operation/path to the configured set of path rules, in case we do the decision is passed on to the random sampler
    * @param operation
    * @return
    */
  override def decide(operation: Sampler.Operation): Trace.SamplingDecision = {
    if(accept(operation.operationName())) sampler.decide(operation) else Trace.SamplingDecision.DoNotSample
  }
}
