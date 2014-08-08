/**
 * Copyright 2002-2014 Evgeny Gryaznov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.textmapper.lapg.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Computes a transitive closure of a set of sets based on the given relations.
 */
class SetsClosure {
	private static final int[] EMPTY_ARRAY = new int[0];

	private final IntegerSets sets = new IntegerSets();
	private final List<SetNode> nodes = new ArrayList<SetNode>();
	private int[][] graph;
	private int[] node_set;

	public SetsClosure() {
	}

	public int addSet(int[] set, Object origin) {
		int i = sets.add(set);
		nodes.add(new SetNode(i, origin));
		return nodes.size() - 1;
	}

	public int addIntersection(int[] nodes, Object origin) {
		SetNode result = new SetNode(-1, origin);
		this.nodes.add(result);
		result.setEdges(nodes);
		return this.nodes.size() - 1;
	}

	public void addDependencies(int node, int... source) {
		nodes.get(node).setEdges(source);
	}

	public int complement(int node) {
		return -1 - node;
	}

	public boolean compute() {
		int size = nodes.size();
		graph = new int[size][];
		node_set = new int[size];
		for (int i = 0; i < size; i++) {
			graph[i] = nodes.get(i).edges;
			if (graph[i] == null) graph[i] = EMPTY_ARRAY;
			node_set[i] = nodes.get(i).index;
		}
		return new TransitiveClosure().run();
	}

	public boolean isComplement(int node) {
		return node_set[node] < 0;
	}

	public int[] getSet(int node) {
		int set = node_set[node];
		return sets.sets[set < 0 ? sets.complement(set) : set];
	}

	public Object[] getErrorNodes() {
		List<Object> list = new ArrayList<Object>();
		for (SetNode node : nodes) {
			if (node.isError) list.add(node.origin);
		}

		return list.toArray();
	}

	private class TransitiveClosure {
		private int[] stack;
		private int[] index;
		private int[] lowlink;
		private boolean[] onstack;
		private int current = 0;
		private int top = 0;
		private boolean hasErrors = false;

		public TransitiveClosure() {
			index = new int[graph.length];
			Arrays.fill(index, -1);
			lowlink = new int[graph.length];
			Arrays.fill(lowlink, 0);
			onstack = new boolean[graph.length];
			Arrays.fill(onstack, false);
			stack = new int[graph.length];
		}

		private boolean run() {
			if (graph.length < 2) return true;

			for (int i = 0; i < graph.length; i++) {
				if (index[i] == -1) strongConnect(i);
			}

			return !hasErrors;
		}

		private void strongConnect(int v) {
			index[v] = current;
			lowlink[v] = current;
			current++;
			stack[top++] = v;
			onstack[v] = true;
			for (int w : graph[v]) {
				if (w < 0) w = -1 - w;
				if (index[w] == -1) {
					strongConnect(w);
					lowlink[v] = Math.min(lowlink[v], lowlink[w]);
				} else if (onstack[w]) {
					lowlink[v] = Math.min(lowlink[v], index[w]);
				}
			}
			if (lowlink[v] == index[v]) {
				int stackSize = top;
				do {
					top--;
				} while (stack[top] != v);
				closure(stackSize - top);
				for (int i = top; i < stackSize; i++) {
					onstack[stack[i]] = false;
				}
			}
		}

		private void closure(int size) {
			if (size == 1 && node_set[stack[top]] == -1) {
				// Intersection.
				int result = sets.complement(IntegerSets.EMPTY_SET);
				for (int node : graph[stack[top]]) {
					int s = node < 0 ? sets.complement(node_set[complement(node)]) : node_set[node];
					result = sets.intersection(result, s);
				}
				node_set[stack[top]] = result;
				return;
			}

			// Union.
			int result = 0;
			for (int i = top; i < top + size; i++) {
				int v = stack[i];
				if (node_set[v] == -1) {
					nodes.get(v).markAsError();
					hasErrors = true;
					continue;
				}

				result = sets.union(result, node_set[v]);
				for (int w : graph[v]) {
					if (w < 0 && size > 1) {
						nodes.get(v).markAsError();
						hasErrors = true;
						break;
					}
					int wNode = w < 0 ? complement(w) : w;
					if (onstack[wNode]) continue;

					result = sets.union(result, w < 0 ? sets.complement(node_set[wNode]) : node_set[wNode]);
				}
			}

			if (hasErrors) return;

			// Update all nodes.
			for (int i = top; i < top + size; i++) {
				node_set[stack[i]] = result;
			}
		}
	}

	private static class SetNode {
		public final int index;    // -1 for intersection nodes
		public final Object origin;
		private int[] edges;  // negative edges mean complement of the target set
		private boolean isError;

		private SetNode(int index, Object origin) {
			this.index = index;
			this.origin = origin;
		}

		public void setEdges(int... target) {
			if (edges != null) throw new IllegalStateException();

			edges = Arrays.copyOf(target, target.length);
		}

		public void markAsError() {
			isError = true;
		}
	}
}