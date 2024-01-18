import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        String fileBoard = args[0];
        String fileMove = args[1];
        String[] boardFileLines = readFile(fileBoard);
        String[] moveFileLines = readFile(fileMove);


        List<String[]> boardLines = splitLines(boardFileLines);
        List<String[]> moveLines2D = splitMoves(moveFileLines);

        String[] arrayMove = moveLines2D.get(0); // in case of couldn't take in 1D, 2D is taken and converted into 1D


        int countLine = boardLines.toArray().length;
        int countColumn = boardLines.get(0).length;
        int moveLength = arrayMove.length;
        int ballX = 0; // current location of ball x-axis
        int ballY = 0; // current location of ball y-axis
        int controlSymbolHole = 0; // checks if the ball gets in a hole, if it does, it would be greater than 0
        int score = 0;  // raw score
        int sum = 0;    // what will be added to the raw score
        int countMove = 0;  // if ball got into hole, how many moves could be played
        int countMoveNoHole = 0;  // if ball do not get into hole, how many moves could be played

        for (int i = 0; i < countLine; i++) {
            for (int j = 0; j < countColumn; j++) {
                if (boardLines.get(i)[j].equals("*")) {
                    ballX = i;
                    ballY = j; // determine the current position on cartesian coordinate as (ballX, ballY)
                }
            } // second for
        } // first for

        // writing initial board
        writeToFile("output.txt", "Game board:", false, true);
        for (String[] lines : boardLines){
            for (String columns : lines){
                writeToFile("output.txt", columns + " ", true, false);
            }
            writeToFile("output.txt", "\n", true, false);
        }

        for (int k = 0; k < moveLength; k++) {  // get through all moves

            if (arrayMove[k].equals("L")) {  // when the move is L
                /**
                 * determinate balls coordinate and move it as desired
                 * after moving, check whether there is hole, wall, pointed balls, or normal balls
                 */
                if (ballY == 0) {
                    ballY = countColumn - 1;   // if * is at the end of the board, return the other side of board
                    if (boardLines.get(ballX)[ballY].equals("W")) {   // new position is wall, so
                        ballY = 1; // determine 1 index away of the edges
                        if (boardLines.get(ballX)[ballY].equals("H")) {
                            boardLines.get(ballX)[0] = " ";
                            controlSymbolHole++;    // if any hole is come across, change it from 0
                            countMove = k; //when it's position that has not been come across with any hole
                            break;        // countMove is set in which move the game is

                        } else if (boardLines.get(ballX)[ballY].equals("R") || boardLines.get(ballX)[ballY].equals("Y") || boardLines.get(ballX)[ballY].equals("B")) {
                            score = score + scoreCalculation(boardLines, ballX, ballY, sum);
                            boardLines.get(ballX)[0] = "X";  // replacing with X if position is R, Y, or B
                            boardLines.get(ballX)[ballY] = "*";
                            countMoveNoHole = k;
                        } else {
                            boardLines.get(ballX)[0] = boardLines.get(ballX)[ballY]; // replacing with X if position is not R, Y, B, H, or W
                            boardLines.get(ballX)[ballY] = "*";
                            countMoveNoHole = k;
                        }

                    } // == W
                    else if (boardLines.get(ballX)[ballY].equals("H")) {	// hole
                        boardLines.get(ballX)[0] = " ";
                        controlSymbolHole++;
                        countMove = k;
                        break;

                    } else if (boardLines.get(ballX)[ballY].equals("R") || boardLines.get(ballX)[ballY].equals("Y") || boardLines.get(ballX)[ballY].equals("B")) {
                        score = score + scoreCalculation(boardLines, ballX, ballY, sum);
                        boardLines.get(ballX)[0] = "X";
                        boardLines.get(ballX)[ballY] = "*";
                        countMoveNoHole = k;
                    } else {
                        boardLines.get(ballX)[0] = boardLines.get(ballX)[ballY];
                        boardLines.get(ballX)[ballY] = "*";
                        countMoveNoHole = k;
                    }

                } else { // main else
                    ballY--;
                    if (boardLines.get(ballX)[ballY].equals("W")) {
                        if (ballY == countColumn - 2) {
                            ballY = 0;
                            if (boardLines.get(ballX)[ballY].equals("H")) {	// hole
                                boardLines.get(ballX)[countColumn - 1] = " ";
                                controlSymbolHole++;
                                countMove = k;
                                break;

                            } else if (boardLines.get(ballX)[ballY].equals("R") || boardLines.get(ballX)[ballY].equals("Y") || boardLines.get(ballX)[ballY].equals("B")) {
                                score = score + scoreCalculation(boardLines, ballX, ballY, sum);
                                boardLines.get(ballX)[countColumn - 1] = "X";
                                boardLines.get(ballX)[ballY] = "*";
                                countMoveNoHole = k;
                            }
                        } else {
                            ballY = ballY + 2;
                            if (boardLines.get(ballX)[ballY].equals("H")) {
                                boardLines.get(ballX)[ballY - 1] = " ";
                                controlSymbolHole++;
                                countMove = k;
                                break;

                            } else if (boardLines.get(ballX)[ballY].equals("R") || boardLines.get(ballX)[ballY].equals("Y") || boardLines.get(ballX)[ballY].equals("B")) {
                                score = score + scoreCalculation(boardLines, ballX, ballY, sum);
                                boardLines.get(ballX)[ballY - 1] = "X";
                                boardLines.get(ballX)[ballY] = "*";
                                countMoveNoHole = k;
                            } else {
                                boardLines.get(ballX)[ballY - 1] = boardLines.get(ballX)[ballY];
                                boardLines.get(ballX)[ballY] = "*"; // if not a special position
                                countMoveNoHole = k;
                            }
                        }
                    } // == W
                    else if (boardLines.get(ballX)[ballY].equals("H")) {
                        boardLines.get(ballX)[ballY + 1] = " ";
                        controlSymbolHole++;
                        countMove = k;
                        break;

                    } else if (boardLines.get(ballX)[ballY].equals("R") || boardLines.get(ballX)[ballY].equals("Y") || boardLines.get(ballX)[ballY].equals("B")) {
                        score += scoreCalculation(boardLines, ballX, ballY, sum);
                        boardLines.get(ballX)[ballY + 1] = "X";
                        boardLines.get(ballX)[ballY] = "*";
                        countMoveNoHole = k;

                    } else {
                        boardLines.get(ballX)[ballY + 1] = boardLines.get(ballX)[ballY];
                        boardLines.get(ballX)[ballY] = "*";
                        countMoveNoHole = k;
                    }
                }

            } else if (arrayMove[k].equals("R")) {   // when the move is R
                if (ballY == countColumn - 1) {
                    ballY = 0;
                    if (boardLines.get(ballX)[ballY].equals("W")) {
                        ballY = countColumn - 2;
                        if (boardLines.get(ballX)[ballY].equals("H")) {
                            boardLines.get(ballX)[countColumn - 1] = " ";
                            controlSymbolHole++;
                            countMove = k;
                            break;

                        } else if (boardLines.get(ballX)[ballY].equals("R") || boardLines.get(ballX)[ballY].equals("Y") || boardLines.get(ballX)[ballY].equals("B")) {
                            score = score + scoreCalculation(boardLines, ballX, ballY, sum);
                            boardLines.get(ballX)[countColumn - 1] = "X";
                            boardLines.get(ballX)[ballY] = "*";
                            countMoveNoHole = k;
                        } else {
                            boardLines.get(ballX)[countColumn - 1] = boardLines.get(ballX)[ballY];
                            boardLines.get(ballX)[ballY] = "*";
                            countMoveNoHole = k;
                        }

                    } // == W
                    else if (boardLines.get(ballX)[ballY].equals("H")) {
                        boardLines.get(ballX)[countColumn - 1] = " ";
                        controlSymbolHole++;
                        countMove = k;
                        break;

                    } else if (boardLines.get(ballX)[ballY].equals("R") || boardLines.get(ballX)[ballY].equals("Y") || boardLines.get(ballX)[ballY].equals("B")) {
                        score = score + scoreCalculation(boardLines, ballX, ballY, sum);
                        boardLines.get(ballX)[countColumn - 1] = "X";
                        boardLines.get(ballX)[ballY] = "*";
                        countMoveNoHole = k;
                    } else {
                        boardLines.get(ballX)[countColumn - 1] = boardLines.get(ballX)[ballY];
                        boardLines.get(ballX)[ballY] = "*";
                        countMoveNoHole = k;
                    }
                } else { // main else
                    ballY++;
                    if (boardLines.get(ballX)[ballY].equals("W")) {
                        if (ballY == 1) {
                            ballY = countColumn - 1;
                            if (boardLines.get(ballX)[ballY].equals("H")) {
                                boardLines.get(ballX)[0] = " ";
                                controlSymbolHole++;
                                countMove = k;
                                break;

                            } else if (boardLines.get(ballX)[ballY].equals("R") || boardLines.get(ballX)[ballY].equals("Y") || boardLines.get(ballX)[ballY].equals("B")) {
                                score = score + scoreCalculation(boardLines, ballX, ballY, sum);
                                boardLines.get(ballX)[0] = "X";
                                boardLines.get(ballX)[ballY] = "*";
                                countMoveNoHole = k;
                            } else {
                                boardLines.get(ballX)[0] = boardLines.get(ballX)[ballY];
                                boardLines.get(ballX)[ballY] = "*";
                                countMoveNoHole = k;
                            }
                        } else {
                            ballY = ballY - 2;
                            if (boardLines.get(ballX)[ballY].equals("H")) {
                                boardLines.get(ballX)[ballY + 1] = " ";
                                controlSymbolHole++;
                                countMove = k;
                                break;

                            } else if (boardLines.get(ballX)[ballY].equals("R") || boardLines.get(ballX)[ballY].equals("Y") || boardLines.get(ballX)[ballY].equals("B")) {
                                score = score + scoreCalculation(boardLines, ballX, ballY, sum);
                                boardLines.get(ballX)[ballY + 1] = "X";
                                boardLines.get(ballX)[ballY] = "*";
                                countMoveNoHole = k;
                            } else {
                                boardLines.get(ballX)[ballY + 1] = boardLines.get(ballX)[ballY];
                                boardLines.get(ballX)[ballY] = "*";
                                countMoveNoHole = k;
                            }
                        }
                    } // == W
                    else if (boardLines.get(ballX)[ballY].equals("H")) {
                        boardLines.get(ballX)[ballY - 1] = " ";
                        controlSymbolHole++;
                        countMove = k;
                        break;

                    } else if (boardLines.get(ballX)[ballY].equals("R") || boardLines.get(ballX)[ballY].equals("Y") || boardLines.get(ballX)[ballY].equals("B")) {
                        score = score + scoreCalculation(boardLines, ballX, ballY, sum);
                        boardLines.get(ballX)[ballY - 1] = "X";
                        boardLines.get(ballX)[ballY] = "*";
                        countMoveNoHole = k;
                    } else {
                        boardLines.get(ballX)[ballY - 1] = boardLines.get(ballX)[ballY];
                        boardLines.get(ballX)[ballY] = "*";
                        countMoveNoHole = k;
                    }
                }

            } else if (arrayMove[k].equals("U")) {   // when the move is U
                if (ballX == 0) {
                    ballX = countLine - 1;
                    if (boardLines.get(ballX)[ballY].equals("W")) {
                        ballX = 1;
                        if (boardLines.get(ballX)[ballY].equals("H")) {
                            boardLines.get(0)[ballY] = " ";
                            controlSymbolHole++;
                            countMove = k;
                            break;

                        } else if (boardLines.get(ballX)[ballY].equals("R") || boardLines.get(ballX)[ballY].equals("Y") || boardLines.get(ballX)[ballY].equals("B")) {
                            score = score + scoreCalculation(boardLines, ballX, ballY, sum);
                            boardLines.get(0)[ballY] = "X";
                            boardLines.get(ballX)[ballY] = "*";
                            countMoveNoHole = k;
                        } else {
                            boardLines.get(0)[ballY] = boardLines.get(ballX)[ballY];
                            boardLines.get(ballX)[ballY] = "*";
                            countMoveNoHole = k;
                        }
                    } // == W
                    else if (boardLines.get(ballX)[ballY].equals("H")) {
                        boardLines.get(0)[ballY] = " ";
                        controlSymbolHole++;
                        countMove = k;
                        break;

                    } else if (boardLines.get(ballX)[ballY].equals("R") || boardLines.get(ballX)[ballY].equals("Y") || boardLines.get(ballX)[ballY].equals("B")) {
                        score = score + scoreCalculation(boardLines, ballX, ballY, sum);
                        boardLines.get(0)[ballY] = "X";
                        boardLines.get(ballX)[ballY] = "*";
                        countMoveNoHole = k;
                    } else {
                        boardLines.get(0)[ballY] = boardLines.get(ballX)[ballY];
                        boardLines.get(ballX)[ballY] = "*";
                        countMoveNoHole = k;
                    }
                } else { // main else
                    ballX = ballX - 1;
                    if (boardLines.get(ballX)[ballY].equals("W")) {
                        if (ballX == countLine - 2) {
                            ballX = 0;
                            if (boardLines.get(ballX)[ballY].equals("H")) {
                                boardLines.get(countLine - 1)[ballY] = " ";
                                controlSymbolHole++;
                                countMove = k;
                                break;

                            } else if (boardLines.get(ballX)[ballY].equals("R") || boardLines.get(ballX)[ballY].equals("Y") || boardLines.get(ballX)[ballY].equals("B")) {
                                score = score + scoreCalculation(boardLines, ballX, ballY, sum);
                                boardLines.get(countLine - 1)[ballY] = "X";
                                boardLines.get(ballX)[ballY] = "*";
                                countMoveNoHole = k;
                            } else {
                                boardLines.get(countLine - 1)[ballY] = boardLines.get(ballX)[ballY];
                                boardLines.get(ballX)[ballY] = "*";
                                countMoveNoHole = k;
                            }
                        } else {
                            ballX = ballX + 2;
                            if (boardLines.get(ballX)[ballY].equals("H")) {
                                boardLines.get(ballX - 1)[ballY] = " ";
                                controlSymbolHole++;
                                countMove = k;
                                break;

                            } else if (boardLines.get(ballX)[ballY].equals("R") || boardLines.get(ballX)[ballY].equals("Y") || boardLines.get(ballX)[ballY].equals("B")) {
                                score = score + scoreCalculation(boardLines, ballX, ballY, sum);
                                boardLines.get(ballX - 1)[ballY] = "X";
                                boardLines.get(ballX)[ballY] = "*";
                                countMoveNoHole = k;
                            } else {
                                boardLines.get(ballX - 1)[ballY] = boardLines.get(ballX)[ballY];
                                boardLines.get(ballX)[ballY] = "*";
                                countMoveNoHole = k;
                            }
                        }
                    } // == W
                    else if (boardLines.get(ballX)[ballY].equals("H")) {
                        boardLines.get(ballX + 1)[ballY] = " ";
                        controlSymbolHole++;
                        countMove = k;
                        break;

                    } else if (boardLines.get(ballX)[ballY].equals("R") || boardLines.get(ballX)[ballY].equals("Y") || boardLines.get(ballX)[ballY].equals("B")) {
                        score = score + scoreCalculation(boardLines, ballX, ballY, sum);
                        boardLines.get(ballX + 1)[ballY] = "X";
                        boardLines.get(ballX)[ballY] = "*";
                        countMoveNoHole = k;
                    } else {
                        boardLines.get(ballX + 1)[ballY] = boardLines.get(ballX)[ballY];
                        boardLines.get(ballX)[ballY] = "*";
                        countMoveNoHole = k;
                    }
                }
            } else if (arrayMove[k].equals("D")) {   // when the move is D
                if (ballX == countLine - 1) {
                    ballX = 0;
                    if (boardLines.get(ballX)[ballY].equals("W")) {
                        ballX = countLine - 2;
                        if (boardLines.get(ballX)[ballY].equals("H")) {
                            boardLines.get(countLine - 1)[ballY] = " ";
                            controlSymbolHole++;
                            countMove = k;
                            break;

                        } else if (boardLines.get(ballX)[ballY].equals("R") || boardLines.get(ballX)[ballY].equals("Y") || boardLines.get(ballX)[ballY].equals("B")) {
                            score = score + scoreCalculation(boardLines, ballX, ballY, sum);
                            boardLines.get(countLine - 1)[ballY] = "X";
                            boardLines.get(ballX)[ballY] = "*";
                            countMoveNoHole = k;
                        } else {
                            boardLines.get(countLine - 1)[ballY] = boardLines.get(ballX)[ballY];
                            boardLines.get(ballX)[ballY] = "*";
                            countMoveNoHole = k;
                        }
                    } // == W
                    else if (boardLines.get(ballX)[ballY].equals("H")) {
                        boardLines.get(countLine - 1)[ballY] = " ";
                        controlSymbolHole++;
                        countMove = k;
                        break;

                    } else if (boardLines.get(ballX)[ballY].equals("R") || boardLines.get(ballX)[ballY].equals("Y") || boardLines.get(ballX)[ballY].equals("B")) {
                        score = score + scoreCalculation(boardLines, ballX, ballY, sum);
                        boardLines.get(countLine - 1)[ballY] = "X";
                        boardLines.get(ballX)[ballY] = "*";
                        countMoveNoHole = k;
                    } else {
                        boardLines.get(countLine - 1)[ballY] = boardLines.get(ballX)[ballY];
                        boardLines.get(ballX)[ballY] = "*";
                        countMoveNoHole = k;
                    }
                } else { // main else
                    ballX++;
                    if (boardLines.get(ballX)[ballY].equals("W")) {
                        if (ballX == 1) {
                            ballX = countLine - 1;
                            if (boardLines.get(ballX)[ballY].equals("H")) {
                                boardLines.get(0)[ballY] = " ";
                                controlSymbolHole++;
                                countMove = k;
                                break;

                            } else if (boardLines.get(ballX)[ballY].equals("R") || boardLines.get(ballX)[ballY].equals("Y") || boardLines.get(ballX)[ballY].equals("B")) {
                                score = score + scoreCalculation(boardLines, ballX, ballY, sum);
                                boardLines.get(0)[ballY] = "X";
                                boardLines.get(ballX)[ballY] = "*";
                                countMoveNoHole = k;
                            } else {
                                boardLines.get(0)[ballY] = boardLines.get(ballX)[ballY];
                                boardLines.get(ballX)[ballY] = "*";
                                countMoveNoHole = k;
                            }
                        } else {
                            ballX = ballX - 2;
                            if (boardLines.get(ballX)[ballY].equals("H")) {
                                boardLines.get(ballX + 1)[ballY] = " ";
                                controlSymbolHole++;
                                countMove = k;
                                break;

                            } else if (boardLines.get(ballX)[ballY].equals("R") || boardLines.get(ballX)[ballY].equals("Y") || boardLines.get(ballX)[ballY].equals("B")) {
                                score = score + scoreCalculation(boardLines, ballX, ballY, sum);
                                boardLines.get(ballX + 1)[ballY] = "X";
                                boardLines.get(ballX)[ballY] = "*";
                                countMoveNoHole = k;
                            }
                            else {
                                boardLines.get(ballX + 1)[ballY] = boardLines.get(ballX)[ballY];
                                boardLines.get(ballX)[ballY] = "*";
                                countMoveNoHole = k;
                            }
                        }
                    } // == W
                    else if (boardLines.get(ballX)[ballY].equals("H")) {
                        boardLines.get(ballX - 1)[ballY] = " ";
                        controlSymbolHole++;
                        countMove = k;
                        break;

                    } else if (boardLines.get(ballX)[ballY].equals("R") || boardLines.get(ballX)[ballY].equals("Y") || boardLines.get(ballX)[ballY].equals("B")) {
                        score = score + scoreCalculation(boardLines, ballX, ballY, sum);
                        boardLines.get(ballX - 1)[ballY] = "X";
                        boardLines.get(ballX)[ballY] = "*";
                        countMoveNoHole = k;
                    } else {
                        boardLines.get(ballX - 1)[ballY] = boardLines.get(ballX)[ballY];
                        boardLines.get(ballX)[ballY] = "*";
                        countMoveNoHole = k;
                    }

                }
            }
        } // first for


        if (controlSymbolHole == 0){
            boardLines.get(ballX)[ballY] = "*";
            writeToFile("output.txt", "\nYour movement is:", true, true);
            for (int m = 0; m < countMoveNoHole + 1; m++){
                writeToFile("output.txt", arrayMove[m] + " ", true, false);
            }
        }
        if (controlSymbolHole != 0){
            writeToFile("output.txt", "\nYour movement is:", true, true);
            for (int m = 0; m < countMove + 1; m++){
                writeToFile("output.txt", arrayMove[m] + " ", true, false);
            }
        }

        writeToFile("output.txt", "\n\nYour output is:", true, true);

        for (String[] lines : boardLines){
            for (String columns : lines){
                writeToFile("output.txt", columns + " ", true, false);
            }
            writeToFile("output.txt", "\n", true, false);
        }
        writeToFile("output.txt", "", true, true);

        if (controlSymbolHole != 0){
            writeToFile("output.txt", "Game Over!", true, true);
        }

        writeToFile("output.txt", "Score: " + score, true, true);
    }

    private static int scoreCalculation(List<String[]> boardLines, int ballX, int ballY, int sum){
        if (boardLines.get(ballX)[ballY].equals("R")){
            sum = 10;
        } else if (boardLines.get(ballX)[ballY].equals("Y")){
            sum = 5;
        }    else if (boardLines.get(ballX)[ballY].equals("B")){
            sum = -5;
        }
        return sum;
    }


    private static List<String[]> splitLines (String[]lines){
        List<String[]> result = new ArrayList<>();
        for (String line : lines) {
            String[] letters = line.split(" ");
            result.add(letters);
        }
        return result;
    }


    private static List<String[]> splitMoves (String[]lines){
        List<String[]> result = new ArrayList<>();
        for (String line : lines) {
            String[] moves = line.split(" ");
            result.add(moves);
        }
        return result;
    }


    private static String[] readFile (String path){    // reading files of move and board
        try {                                          // store in an array of String
            int i = 0;
            int length = Files.readAllLines(Paths.get(path)).size();
            String[] results = new String[length];      // length is not restricted, it is along the file
            for (String line : Files.readAllLines(Paths.get(path))) {    // result stores lines of file
                results[i++] = line;
            }
            return results;
        } catch (IOException e) { //Returns null if there is no such a file.
            e.printStackTrace();
            return null;
        }
    }

    private static void writeToFile (String path, String content,boolean append, boolean newLine){
        /**
         * This function writes given content to file at given path.
         *
         * @param path    Path for the file content is going to be written.
         * @param content Content that is going to be written to file.
         * @param append  Append status, true if wanted to append to file if it exists, false if wanted to create file from zero.
         * @param newLine True if wanted to append a new line after content, false if vice versa.
         */
        PrintStream ps = null;
        try {
            ps = new PrintStream(new FileOutputStream(path, append));
            ps.print(content + (newLine ? "\n" : ""));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) { //Flushes all the content and closes the stream if it has been successfully created.
                ps.flush();
                ps.close();
            }
        }
    }
}

