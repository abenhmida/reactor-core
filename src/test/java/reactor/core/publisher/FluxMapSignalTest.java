/*
 * Copyright (c) 2011-2016 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.core.publisher;

import org.junit.Test;
import reactor.test.StepVerifier;
import reactor.test.subscriber.AssertSubscriber;

public class FluxMapSignalTest {
    @Test
    public void completeOnlyBackpressured() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create(0L);
        
        new FluxMapSignal<>(Flux.empty(), null, null, () -> 1)
        .subscribe(ts);
        
        ts.assertNoValues()
        .assertNoError()
        .assertNotComplete();
        
        ts.request(1);
        
        ts.assertValues(1)
        .assertNoError()
        .assertComplete();
    }

    @Test
    public void errorOnlyBackpressured() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create(0L);
        
        new FluxMapSignal<>(Flux.error(new RuntimeException()), null, e -> 1, null)
        .subscribe(ts);
        
        ts.assertNoValues()
        .assertNoError()
        .assertNotComplete();
        
        ts.request(1);
        
        ts.assertValues(1)
        .assertNoError()
        .assertComplete();
    }

	@Test
	public void flatMapSignal() {
		StepVerifier.create(Flux.just(1, 2, 3)
		                        .flatMap(d -> Flux.just(d * 2),
				                        e -> Flux.just(99),
				                        () -> Flux.just(10)))
		            .expectNext(2, 4, 6, 10)
		            .verifyComplete();
	}

	@Test
	public void flatMapSignalError() {
		StepVerifier.create(Flux.just(1, 2, 3).concatWith(Flux.error(new Exception("test")))
		                        .flatMap(d -> Flux.just(d * 2),
				                        e -> Flux.just(99),
				                        () -> Flux.just(10)))
		            .expectNext(2, 4, 6, 99)
		            .verifyComplete();
	}

	@Test
	public void flatMapSignal2() {
		StepVerifier.create(Mono.just(1)
		                        .flatMap(d -> Flux.just(d * 2),
				                        e -> Flux.just(99),
				                        () -> Flux.just(10)))
		            .expectNext(2, 10)
		            .verifyComplete();
	}
}
