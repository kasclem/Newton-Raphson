import java.util.HashMap;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class Methods {
	private static String opStr = "+ - * / ^ ~ sin cos tan";
	private static String[] opArr = opStr.split(" ");
	private static int[] priority = {3, 3, 2, 2, 1, 0, 0, 0, 0};
	
	static String exp = "2sin(x + 2)";
	static String derivative = "8*x + 2";
	public static void main(String[] args) {
		
		System.out.println(evaluate(exp, 0.5));
		
		}
	
	/*
	 * A+B*-C-D

	 * ABC
	 * +*-
	 * breaks with 4sin(x)
	 * */
	public static String getTangentFunction(String in, double x) {
		double y1 = evaluate(in, x);
		double m = getSlope(in, x);
		return String.format(" %f*(x-%f) + %f", m, x, y1 );
	}
	
	public static double getSlope(String in, double x) {
		double delta = 0.00000001;
		double rise = evaluate(in, x+delta) - evaluate(in, x);
		double m1 = rise/delta;
		rise = evaluate(in, x) - evaluate(in, x-delta);
		double m2 = rise/delta;
		return (m1+m2)/2;
	}
	
	//Ginormous method:
	public static double evaluate(String in, double x) {
		in = in.replaceAll(" ", "");
		in = addAsterisks(in);
		Stack<String> operator = new Stack<>();
		Stack<Double> stValue = new Stack<>();
		StringBuilder sb = new StringBuilder();		
		StringTokenizer tk = new StringTokenizer(in, "+-*/^()", true);
		String curr = "(", previous;

		while(tk.hasMoreTokens()) {
			previous = curr;
			curr = tk.nextToken();

			if(curr.equals("(")) operator.push(curr);
			else if(curr.equals(")")) {
				while(!operator.isEmpty() && 
						!operator.peek().equals("(")
						) {
					apply(operator.pop(), operator, stValue);
				}
				operator.pop();
			}
			else if(isOperator(curr)) {
				if(isUnaryOperator(curr, previous)) {
					if(!operator.isEmpty() && isUnaryOperator(operator.peek())) apply(operator.pop(), operator, stValue);
					if(curr.equals("-")) operator.push("~");
					else operator.push(curr);
				}else {
					while(!operator.isEmpty() && 
							!operator.peek().equals("(") &&
							getPriority(curr)>getPriority(operator.peek()) ) {
						apply(operator.pop(), operator, stValue);
					}
					operator.push(curr);
				}
			}else {
				if(curr.equals("x")) {
					stValue.push(x);
				}else {
					try {
						stValue.push(Double.parseDouble(curr));
					}catch (NumberFormatException e) {
						System.out.println("invalid input");
					}					
				}
			}
		}

		while(!operator.isEmpty() && 
				!operator.peek().equals("(")
				) {
			apply(operator.pop(), operator, stValue);
		}
		if(stValue.size()!=1 || operator.size()>0) {
			throw new ArithmeticException("Invalid Expression");
		}
		return stValue.pop();
	}
	private static String addAsterisks(String in) {
		StringBuilder sb = new StringBuilder(in);
		for(int i=0 ; i<sb.length() ; i++) {
			char curr = sb.charAt(i);
			if((curr=='(' || Character.isLetter(curr)) && i>0 && Character.isDigit(in.charAt(i-1))) {
				sb.insert(i, '*');
			}
		}
		return sb.toString();
	}

	private static int getPriority(String curr) {
		switch(curr) {
		case "+": return 3;
		case "-": return 3;
		case "*": return 2;
		case "/": return 2;
		case "^": return 1;
		case "~": return 0;
		case "sin": return 0;
		case "cos": return 0;
		case "tan": return 0;
		default:
			System.out.println("invalid priority");
		}
		return 5;
	}
	private static boolean isOperator(String curr) {
		for(String x : opArr) {
			if(curr.equals(x)) return true;
		}
		return false;
	}
	private static boolean isUnaryOperator(String peek) {
		return peek.equals("~") || peek.equals("sin") || peek.equals("cos") || peek.equals("tan");
	}
	private static boolean isUnaryOperator(String curr, String previous) {
		if(curr.equals("-") && 
				(previous.equals("(") || isOperator(previous))) return true;
		return isUnaryOperator(curr);
	}
	private static void apply(String operator, Stack<String> operators, Stack<Double> stValue) {
		double first = stValue.pop();
		
		//unary operators
		switch(operator) {
		case "~": 
			stValue.push(-first); 
			return;
		case "sin":
			stValue.push(Math.sin(first));
			return;
		case "cos":
			stValue.push(Math.cos(first));
			return;
		case "tan":
			stValue.push(Math.tan(first));
			return;
		default:
		}
		double second = stValue.pop();
		switch(operator) {
		case "+":
			stValue.push(second+first);
			break;
		case "-":
			stValue.push(second-first);
			break;
		case "*":
			stValue.push(second*first);
			break;
		case "/":
			stValue.push(second/first);
			break;
		case "^":
			stValue.push(Math.pow(second, first));
			break;
		}
	}
}
