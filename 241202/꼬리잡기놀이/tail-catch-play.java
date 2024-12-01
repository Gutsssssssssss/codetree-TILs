import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {

	/*
	 * 
	 * */
	static int N, M, K, ret;
	static int[][] arr;
	static boolean[][] visited;
	static int[] dy = {0, -1, 0, 1};
	static int[] dx = {1, 0, -1, 0};
	static Map<Integer, Deque<int[]>> teams;
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		
		teams = new HashMap<>();
		
		arr = new int[N][N];
		visited = new boolean[N][N];
		for (int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < N; j++) {
				arr[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		int teamNum = 5;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (arr[i][j] == 1 && !visited[i][j]) {
					bfs(i, j, teamNum);
					teamNum++;
				}
			}
		}
		
		
		// [1] 이동
		for (int r = 0; r < K; r++) {
			for (Deque<int[]> team : teams.values()) {
				int[] tail = team.pollLast();
				arr[tail[0]][tail[1]] = 4;
				int[] head = team.peekFirst();
				for (int i = 0;i < 4; i++) {
					int ny = head[0] + dy[i];
					int nx = head[1] + dx[i];
					if (ny >= 0 && ny < N && nx >= 0 && nx < N && arr[ny][nx] == 4) {
						arr[ny][nx] = arr[head[0]][head[1]];
						team.addFirst(new int[] {ny, nx});
						break;
					}
				}
			}
			
			// [2] 공 던지기
			int dr = (r / N) % 4;
			int cy, cx;
			int offset = (r % N);
			if (dr == 0) {
				cy = offset;
				cx = 0;
			} else if (dr == 1) {
				cy = N - 1;
				cx = offset;
			} else if (dr == 2) {
				cy = (N - 1) - offset;
				cx = N - 1;
			} else {
				cy = 0;
				cx = (N - 1) - offset;
			}
			
			for (int i = 0; i < N; i++) {
				if (cy >= 0 && cy < N && cx >= 0 && cx < N && arr[cy][cx] >= 5) {
					int teamId = arr[cy][cx];
					Deque<int[]> team = teams.get(teamId);
					
					int idx = 1;
					for (int[] member : team) {
						if (cy == member[0] && cx == member[1]) {
							ret += (idx * idx);
							break;
						}
						idx++;
					}
					reverseDq(team);
					break;
				}
				
				cy = cy + dy[dr];
				cx = cx + dx[dr];
			}
		} // round
		
		System.out.println(ret);
	} // main
	
	static void reverseDq(Deque<int[]> team) {
		LinkedList<int[]> temp = new LinkedList<int[]>(team);
		Collections.reverse(temp);
		team.clear();
		team.addAll(temp);
	}

	static void debug() {
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				System.out.print(arr[i][j] + " ");
			}System.out.println();
		}System.out.println();
	}
	static void bfs(int y, int x, int teamNum) {
		Queue<int[]> q = new ArrayDeque<int[]>();
		Deque<int[]> dq = new ArrayDeque<int[]>();
		
		arr[y][x] = teamNum;
		
		q.add(new int[] {y, x});
		dq.add(new int[] {y, x});
		visited[y][x] = true;
		
		while(!q.isEmpty()) {
			int[] cur = q.poll();
			int cy = cur[0];
			int cx = cur[1];
			for (int i = 0; i < 4; i++) {
				int ny = cy + dy[i];
				int nx = cx + dx[i];
				// 범위 체크하고 다음이 2인 경우 
				if (ny >= 0 && ny < N && nx >= 0 && nx < N && !visited[ny][nx]) {
					if (arr[ny][nx] == 2 || (arr[ny][nx] == 3 && !(cy == y && cx == x))) {
						q.add(new int[] {ny, nx});
						visited[ny][nx] = true;
						dq.add(new int[] {ny, nx});
						arr[ny][nx] = teamNum;
					}
				}
			}
		}
		teams.put(teamNum, dq);
	} // bfs
} // class
