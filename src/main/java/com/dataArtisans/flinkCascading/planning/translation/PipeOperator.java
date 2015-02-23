/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dataArtisans.flinkCascading.planning.translation;

import cascading.flow.planner.Scope;
import cascading.pipe.Pipe;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;

import java.util.List;


public class PipeOperator extends Operator {

	public PipeOperator(Pipe pipe, Scope incomingScope, Scope outgoingScope, Operator inputOp) {
		super(inputOp, incomingScope, outgoingScope);

		// TODO: check if we can remove this!!
//		setIncomingScope(inputOp.getOutgoingScope());
//		Scope outgoing = pipe.outgoingScopeFor(Collections.singleton(getIncomingScope()));
//		outgoing.setName(pipe.getName());
//		setOutgoingScope(outgoing);

	}

	protected DataSet translateToFlink(ExecutionEnvironment env, List<DataSet> inputs) {

		return inputs.get(0);
	}


}
