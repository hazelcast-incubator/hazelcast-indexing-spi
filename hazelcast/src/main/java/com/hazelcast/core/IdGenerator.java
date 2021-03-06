/*
 * Copyright (c) 2008-2016, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.core;

/**
 * The IdGenerator is responsible for creating unique ids (a {@code long}) in a cluster.
 *
 * In theory a {@link com.hazelcast.core.IAtomicLong#incrementAndGet()} could be used to provide the same functionality.
 * The big difference is that the incrementAndGet requires one or more remote calls for every invocation and therefor
 * is a performance and scalability bottleneck. The IdGenerator uses an IAtomicLong under the hood, but instead of
 * doing remote call for every call to {@link #newId()}, it does it less frequently. It checks out a chunk, e.g. 1..1000 and
 * as long as it has not yet consumed all the ids in its chunk, then no remote call is done.
 *
 * It can be that ids generated by different cluster members will get out of order because each member will get its own chunk. It
 * can be that member 1 has chunk 1..1000 and member 2 has 1001..2000. Therefore, member 2 will automatically have ids that
 * are out of order with the ids generated by member 1.
 */
public interface IdGenerator extends DistributedObject {

    /**
     * Try to initialize this IdGenerator instance with the given id. The first
     * generated id will be 1 greater than id.
     *
     * @return true if initialization succeeded, false if id is less than 0.
     */
    boolean init(long id);

    /**
     * Generates and returns a cluster-wide unique id.
     * Generated ids are guaranteed to be unique for the entire cluster
     * as long as the cluster is live. If the cluster restarts, then
     * id generation will start from 0.
     *
     * @return the cluster-wide new unique id
     */
    long newId();
}
