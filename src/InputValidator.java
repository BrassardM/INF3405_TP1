import java.util.Scanner;

public class InputValidator {
	private enum StringState{
		FIRST_BYTE, 
		SECOND_BYTE,
		THIRD_BYTE,
		FOURTH_BYTE,
		INVALID;

		public StringState next() {
			switch (this) {
			case FIRST_BYTE : return SECOND_BYTE;
			case SECOND_BYTE : return THIRD_BYTE;
			case THIRD_BYTE : return FOURTH_BYTE;
			case FOURTH_BYTE : return INVALID;
			default : return INVALID;
			}
		}
		
		public boolean checkValid() {
			if (this == FOURTH_BYTE) {
				return true;
			}
			else {
				return false;
			}
		}
	}

	private enum ByteState{
		FIRST_NUM, 
		SECOND_NUM,
		THIRD_NUM,
		INVALID;

		public ByteState next() {
			switch (this) {
			case FIRST_NUM : return SECOND_NUM;
			case SECOND_NUM : return THIRD_NUM;
			case THIRD_NUM : return INVALID;
			default : return INVALID;
			}
		}
		
		public boolean checkValid() {
			if (this == INVALID) {
				return false;
			}
			else {
				return true;
			}
		}
		public boolean isFirst() {
			if (this == FIRST_NUM) {
				return true;
			}
			else {
				return false;
			}
		}

	}


	private boolean isValid(final String input) {
		ByteState byteState = ByteState.FIRST_NUM;
		StringState stringState = StringState.FIRST_BYTE;
		int currentNum = 0;

		for (char c : input.toCharArray()) {
			if (c == '.' && (!byteState.isFirst())) {
				if (currentNum > 255) {
					System.err.format("!! MAX BYTE SIZE IS 255, [%d] HAS BEEN ENTERED !!",currentNum);
					return false;
				}
				stringState = stringState.next();
				byteState = ByteState.FIRST_NUM;
				currentNum = 0;
			}
			else if (Character.isDigit(c) && byteState.checkValid()) {
				if (currentNum == 0 && !byteState.isFirst()) {
					System.err.print("!! LEADING ZEROS : NOT ALLOWED !!");
					return false;
				}
				currentNum*= 10;
				currentNum += Character.getNumericValue(c);
				byteState = byteState.next();				
			}
			else {
				System.err.format("!! INVALID CHARACTER : %c !!",c);
				return false;
			}
		}
		return ((currentNum <= 255) && stringState.checkValid() && (!byteState.isFirst()));
	}
	
	public String inputIP() {
		System.out.print("Input the server's IP address : ");
		Scanner scanner = new Scanner(System.in);
		String input = scanner.nextLine();
		if (isValid(input)) {
			System.out.format("The IP address : [%s] is valid %n",input);
			return input;
		}
		else {
			System.err.format("The IP address : [%s] is invalid%n",input);
			return inputIP();
		}
	}
	
	public int inputPort() {
		System.out.print("Input the server's port (between 5000 & 5050) : ");
		Scanner scanner = new Scanner(System.in);
		int input = scanner.nextInt();
		if (input >= 5000 && input <= 5050) {
			System.out.format("The port %d is valid.%n", input);
			return input;
		}
		else {
			System.err.format("The port %d is invalid.%n",input);
			return inputPort();
		}
	}
	
}