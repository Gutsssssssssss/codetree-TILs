import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.util.ArrayDeque;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
	/*
	 * 루돌프와 산타(1번부터 P번, 기절과 탈락은 제외)순서대로 이동
	 * 1. 루돌프: 가장 가까운 산타에게 돌진(행큰 -> 열큰), 인접 8방향 중 하나로 한 칸 이동
	 * 충돌시, 산타 C 점수 얻고 C 만큼 밀려남, 밀려난 곳에 산타 있으면 상호작용
	 * 2. 산타: 루돌프에 가까워지는 방향으로 이동(산타 있으면 불가, 이동할 수 있어도 가까워지지 않으면 불가, 상->우-하->좌)
	 * 충돌시, 산타 D 점수 얻고 D 만큼 밀려남, 밀려난 곳에 산타 있으면 상호작용(1칸 밀림 연쇄)
	 * 3. 기절(이동 못함): 현재 k턴이면 k+2턴에서 정상됨. 기절한 산타도 루돌프의 돌진 대상
	 *
	 * M턴(모두 탈락시 즉시 종료)동안 매턴 미탈락 산타에 1점 부여
	 * 3<=N<=50, 1<=M<=1000, 1<=P<=30
	 * 
	 * */
	static int[] rdy = {-1, -1, 0, 1, 1, 1, 0, -1};
	static int[] rdx = {0, 1, 1, 1, 0, -1, -1, -1};
	static int[] sdy = {-1, 0, 1, 0};
	static int[] sdx = {0, 1, 0, -1};
	static int N, M, P, C, D;
	static int ry, rx;
	static int[][] santa, sturn, v, point;
	static boolean[] dead;
	public static void main(String[] args) throws IOException {
		// 입력
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		P = Integer.parseInt(st.nextToken());
		C = Integer.parseInt(st.nextToken());
		D = Integer.parseInt(st.nextToken());
		st = new StringTokenizer(br.readLine());
		ry = Integer.parseInt(st.nextToken());
		rx = Integer.parseInt(st.nextToken());
		santa = new int[P+1][2];
		sturn = new int[P+1][1];
		v = new int[N][N];
		dead = new boolean[P+1];
		point = new int[P+1][1];
		ry--;
		rx--;
		v[ry][rx] = -1;
		for(int i=0; i<P; i++) {
			st = new StringTokenizer(br.readLine());
			int num = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			int x = Integer.parseInt(st.nextToken());
			y -= 1;
			x -= 1;
			santa[num][0] = y;
			santa[num][1] = x;
			v[y][x] = num;
		}
		for(int turn=1; turn<=M; turn++) {
			int deadCnt = 0;
			for(int i=1; i<=P; i++) {
				if(dead[i]) {
					deadCnt++;
					
				}
			}
			if(deadCnt == P) break;
			// 루돌프 이동
			int minDist = N*N*2;
			int yDir = -1;
			int xDir = -1;
			PriorityQueue<Node> pq = new PriorityQueue<>(); 
			for(int i=1; i<=P; i++) {
				if(dead[i]) continue;
				int curDist = calDist(ry, rx, santa[i][0], santa[i][1]);
				if(minDist >= curDist) {
					minDist = curDist;
					yDir = santa[i][0] - ry;
					xDir = santa[i][1] - rx;
					Node node = new Node(santa[i][0], santa[i][1]);
					node.yDir = yDir;
					node.xDir = xDir;
					node.dist = minDist;
					pq.add(node);
				}
			}
			v[ry][rx] = 0;
			Node node = pq.poll();
			yDir = node.yDir;
			xDir = node.xDir;
			if(yDir != 1 && yDir != 0 && yDir != -1) {
				yDir = yDir / Math.abs(yDir);
			} 
			if(xDir != 1 && xDir != 0 && xDir != -1) {
				xDir = xDir / Math.abs(xDir);
			} 
			ry = ry + yDir;
			rx = rx + xDir;
			
			if(v[ry][rx] != 0) {
				crash(ry, rx, yDir, xDir, C, turn);
			}
			v[ry][rx] = -1;
//			System.out.println("==="+turn + "turn loo===" );
//			for(int i=0; i<N; i++) {
//				for(int j=0; j<N; j++) {
//					System.out.print(v[i][j] + " ");
//				} System.out.println();
//			}System.out.println("===================");
			// 산타 이동
			for(int i=1; i<=P; i++) {
				if(dead[i] || sturn[i][0]>turn) continue;
				int curDist = calDist(ry, rx, santa[i][0], santa[i][1]);
				int minDir = -1;
				for(int j=0; j<4; j++) {
					int ny = santa[i][0] + sdy[j];
					int nx = santa[i][1] + sdx[j];
					if(ny<0 || ny>=N || nx<0 || nx>=N || v[ny][nx]>0) continue;
					int nextDist = calDist(ry, rx, ny, nx);
					if(curDist > nextDist) {
						curDist = nextDist;
						minDir = j;
					}
				}
				if(minDir == -1) continue;
				v[santa[i][0]][santa[i][1]] = 0;
				santa[i][0] = santa[i][0] + sdy[minDir];
				santa[i][1] = santa[i][1] + sdx[minDir];
				if(v[santa[i][0]][santa[i][1]] == -1) {
					v[santa[i][0]][santa[i][1]] = i;
					int tempy = santa[i][0];
					int tempx = santa[i][1];
					crash(santa[i][0], santa[i][1], -sdy[minDir], -sdx[minDir], D, turn);
					v[tempy][tempx] = -1;
				} else {
					v[santa[i][0]][santa[i][1]] = i;
				}
			}
//			System.out.println("==="+turn + "turn santa===" );
//			for(int i=0; i<N; i++) {
//				for(int j=0; j<N; j++) {
//					System.out.print(v[i][j] + " ");
//				} System.out.println();
//			}System.out.println("===================");
			for(int i=1; i<=P; i++) {
				if(dead[i]) continue;
				point[i][0]++;
			}
		}
		for(int i=1; i<=P; i++) {
			System.out.print(point[i][0] + " ");
		}
	} // main
	static void crash(int ry, int rx, int yDir, int xDir, int poi, int turn) {
		int num = v[ry][rx];
		sturn[num][0] = turn + 2;
		point[num][0] += poi;
		Queue<int[]> q = new ArrayDeque<>();
		q.add(new int[] {ry, rx, num});
		while(!q.isEmpty()) {
			int[] san = q.poll();
			int ny = san[0] + yDir*poi;
			int nx = san[1] + xDir*poi;
			num = san[2];
			if(ny<0 || ny>=N || nx<0 || nx>=N) {
				dead[num] = true;
				continue;
			}
			if(v[ny][nx] > 0) {
				int temp = v[ny][nx];
				v[ny][nx] = num;
				q.add(new int[] {ny, nx, temp});
				santa[num][0] = ny;
				santa[num][1] = nx;
			} else {
				v[ny][nx] = num;
				santa[num][0] = ny;
				santa[num][1] = nx;
			}
			poi = 1;
		}
	}
	
	static class Node implements Comparable<Node>{
		int y, x, yDir, xDir, dist;
		public Node(int y, int x) {
			this.y = y;
			this.x = x;
		}
		@Override
		public int compareTo(Node o) {
			if(this.dist == o.dist) {
				if(o.y == this.y) return Integer.compare(o.x, this.x);
				return Integer.compare(o.y, this.y);
			}
			
			return Integer.compare(this.dist, o.dist);
		}
		
	}
	static int calDist(int ry, int rx, int sy, int sx) {
		return (ry-sy)*(ry-sy) + (rx-sx)*(rx-sx);
	}
} // class