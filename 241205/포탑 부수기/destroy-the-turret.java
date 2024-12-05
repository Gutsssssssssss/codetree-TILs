

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {

	static int N, M, K;
	static int[][] arr, turn;
	static int[] dy = {0, 1, 0, -1};
	static int[] dx = {1, 0, -1, 0};
	static int[] ddy = {-1, -1, -1, 0, 1, 1, 1, 0};
	static int[] ddx = {-1, 0, 1, 1, 1, 0, -1, -1};
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		arr = new int[N][M];
		turn = new int[N][M];
		
		for (int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < M; j++) {
				arr[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		for (int T = 1; T <= K; T++) {
			
			
			// [1] 공격자 선정: 공격력 낮은 -> 가장 최근 공격자 -> 행+열(큰) -> 열(큰)
			int mn = 5001; int mxTurn = 0; int sy = -1; int sx = -1;
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < M; j++) {
					if (arr[i][j] <= 0) continue;
					if (mn > arr[i][j] || (mn == arr[i][j] && mxTurn < turn[i][j])
							|| (mn == arr[i][j] && mxTurn == turn[i][j] && i + j > sy + sx)
							|| (mn == arr[i][j] && mxTurn == turn[i][j] && i + j == sy + sx && j > sx)) {
						mn = arr[i][j];
						mxTurn = turn[i][j];
						sy = i;
						sx = j;
					}
				}
			}
			
			
			// [2] 공격 당할 포탑 선정
			int mx = 0; int mnTurn = T; int ey = N; int ex = M;
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < M; j++) {
					if (arr[i][j] <= 0) continue;
					if (mx < arr[i][j] || (mx == arr[i][j] && mnTurn > turn[i][j])
							|| (mx == arr[i][j] && mnTurn == turn[i][j] && i + j < ey + ex)
							|| (mx == arr[i][j] && mnTurn == turn[i][j] && i + j == ey + ex && j < ex)) {
						mx = arr[i][j];
						mnTurn = turn[i][j];
						ey = i;
						ex = j;
					}
				}
			}
			
			
			arr[sy][sx] += (M + N);
			turn[sy][sx] = T;
			
			
			// [2-2] 레이저 공격 (우하좌상 순서로 최단거리 이동-BFS)
			HashSet<String> fset = new HashSet<String>();
			fset.add(sy+","+sx);
			fset.add(ey+","+ex);
			if (!bfs(sy, sx, ey, ex, fset)) { // 레이저 공격 실패
				bomb(sy, sx, ey, ex, fset);
				
			}
			
			// [3] 포탑 정비
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < M; j++) {
					if (arr[i][j] >= 1 && !fset.contains(i+","+j)) {
						arr[i][j] += 1;
					}
				}
			}
			int cnt = 0;
            for (int[] row : arr) {
                for (int cell : row) {
                    if (cell > 0) cnt++;
                }
            }
            if (cnt <= 1) break;
		} // T
		
		int ret = 0;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < M; j++) {
				if (ret < arr[i][j]) {
					ret = arr[i][j];
				}
			}
		}
		System.out.println(ret);
	}
	
	private static void bomb(int sy, int sx, int ey, int ex, HashSet<String> fset) {
		int damage = arr[sy][sx];
		arr[ey][ex] = Math.max(0, arr[ey][ex] - damage);
		fset.add(sy+","+sx);
		fset.add(ey+","+ex);
		for (int i = 0; i < 8; i++) {
			int ny = (ey + ddy[i] + N) % N;
			int nx = (ex + ddx[i] + M) % M;
			if (sy == ny && sx == nx) continue;
			if (arr[ny][nx] >= 1) {
				fset.add(ny+","+nx);
				arr[ny][nx] = Math.max(0, arr[ny][nx] - damage / 2);
			}
		}
		
	}

	private static boolean bfs(int sy, int sx, int ey, int ex, HashSet<String> fset) {
		int[][][] visited = new int[N][M][2];
		Queue<int[]> q = new ArrayDeque<int[]>();
		int damage = arr[sy][sx];
		
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < M; j++) {
				Arrays.fill(visited[i][j], -1);
			}
		}
		visited[sy][sx][0] = sy;
		visited[sy][sx][1] = sx;
		q.add(new int[] {sy, sx});
		fset.add(sy+","+sx);
		
		while(!q.isEmpty()) {
			int[] cur = q.poll();
			int cy = cur[0];
			int cx = cur[1];
			
			if (cy == ey && cx == ex) {
				arr[cy][cx] = Math.max(0, arr[cy][cx] - damage);
				while (true) {
					int py = visited[cy][cx][0];
					int px = visited[cy][cx][1];
					if (py == sy && px == sx) {
						return true;
					}
					arr[py][px] = Math.max(0, arr[py][px] - damage/2);
					fset.add(py+","+px);
					cy = py;
					cx = px;
				}
			}
			
			for (int i = 0; i < 4; i++) {
				int ny = (cy + dy[i] + N) % N;
				int nx = (cx + dx[i] + M) % M;
				if (arr[ny][nx] >= 1 && visited[ny][nx][0] == -1) {
					q.add(new int[] {ny, nx});
					visited[ny][nx][0] = cy;
					visited[ny][nx][1] = cx;
				}
			}
		}
		return false;
	}
	
} // class 
