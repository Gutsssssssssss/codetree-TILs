

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class Main {

	static int N, M;
	static boolean[][] end;
	static int[][] arr;
	static Map<Integer, int[]> marts;
	static Queue<int[]> bcQ;
//	static List<int[]> men;
	static Map<Integer, int[]> men;
	static int[] dy = {-1, 0, 0, 1};
	static int[] dx = {0, -1, 1, 0};
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		
		arr = new int[N][N];
		end = new boolean[N][N];
		
		for (int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < N; j++) {
				arr[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		men = new HashMap<Integer, int[]>();
		marts = new HashMap<Integer, int[]>();
		for (int i = 0; i < M; i++) {
			st = new StringTokenizer(br.readLine());
			marts.put(i, new int[] {
					Integer.parseInt(st.nextToken())-1,
					Integer.parseInt(st.nextToken())-1
			});
		}
		
		// 각 턴
		int turn = 0;
		while(true) {
			// [1] 이동하기 
			Iterator<Map.Entry<Integer, int[]>> iterator = men.entrySet().iterator();
			while (iterator.hasNext()) {
			    Map.Entry<Integer, int[]> entry = iterator.next();
			    Integer idx = entry.getKey();
			    int[] man = entry.getValue();
			    int[] mart = marts.get(idx);
			    
			    int cy = man[0];
			    int cx = man[1];
			    int d = -1;
			    int min = 500;
			    for (int i = 0; i < 4; i++) {
			        int ny = cy + dy[i];
			        int nx = cx + dx[i];
			        if (ny < 0 || ny >= N || nx < 0 || nx >= N || end[ny][nx]) continue;
			        int dist = bfs(ny, nx, mart[0], mart[1]);
			        if (dist <= min) {
			            d = i;
			            min = dist;
			        }
			    }
			    int ny = cy + dy[d];
			    int nx = cx + dx[d];
			    man[0] = ny;
			    man[1] = nx;
			    if (ny == mart[0] && nx == mart[1]) {
			        end[ny][nx] = true;
			        iterator.remove(); // men에서 현재 사람 제거
			        
			    }
			}
			
			// [3] t번 사람 bc에 배치
			if (turn < M) {
				PriorityQueue<BaseCamp> pq = new PriorityQueue<BaseCamp>();
				int[] m = marts.get(turn);
				int cy = m[0];
				int cx = m[1];
				
				int[][] visited = new int[N][N];
				Queue<int[]> q = new ArrayDeque<int[]>();
				
				q.add(new int[] {cy, cx});
				visited[cy][cx] = 1;
				
				while (!q.isEmpty()) {
					int[] cur = q.poll();
					for (int i = 0; i < 4; i++) {
						int ny = cur[0] + dy[i];
						int nx = cur[1] + dx[i];
						if (ny < 0 || ny >= N || nx < 0 || nx >= N || visited[ny][nx] != 0|| end[ny][nx]) continue;
						visited[ny][nx] = visited[cur[0]][cur[1]] + 1;
						q.add(new int[] {ny, nx});
						if (arr[ny][nx] == 1) {
							pq.add(new BaseCamp(ny, nx, visited[ny][nx] - 1));
						}
						
					}
				} // q
				BaseCamp bc = pq.poll();
				men.put(turn, new int[] {bc.y, bc.x});
				end[bc.y][bc.x] = true;
				
			}
			// [4] 편의점에 다 들어오면 종료하기 출력하
			turn++;
			if (men.isEmpty()) {
				System.out.println(turn);
				break;
			}
			
		} // turn
	} // main
	private static int bfs(int y, int x, int my, int mx) {
		int[][] visited = new int[N][N];
		Queue<int[]> q = new ArrayDeque<int[]>();
		visited[y][x] = 1;
		q.add(new int[] {y, x});
		int ret = 0;
		while(!q.isEmpty()) {
			int[] cur = q.poll();
			for (int i = 0; i < 4; i++) {
				int ny = cur[0] + dy[i];
				int nx = cur[1] + dx[i];
				if (ny < 0 || ny >= N || nx < 0 || nx >= N || visited[ny][nx] != 0 || end[ny][nx]) continue;
				visited[ny][nx] = visited[cur[0]][cur[1]] + 1;
				q.add(new int[] {ny, nx});
				if (ny == my && nx == mx) {
					ret = visited[ny][nx] - 1;
					break;
				}
			}
		}
		return ret;
	}
	static class BaseCamp implements Comparable<BaseCamp>{
		int y, x, dist;
		public BaseCamp(int y, int x, int dist) {
			this.y = y;
			this.x = x;
			this.dist = dist;
		}
		@Override
		public int compareTo(BaseCamp o) {
			if (this.dist == o.dist) {
				if (this.y == o.y) {
					return Integer.compare(this.x, o.x);
				}
				return Integer.compare(this.y, o.y);
			}
			return Integer.compare(this.dist, o.dist);
		}
	}
} // class
