import numpy as np
import math


class ElementaryNode():
    """
    Class describing a leaf in a Newick tree, only contains the alphabetic name
    and the attribute minimum which is set to zero and exists for the sole purpose
    of being compatible with the NewickNode class constructor.
    """
    def __init__(self, name):
        self.name = name
        self.minimum = 0.


class NewickNode():
    """
    Class describing a node in a Newick tree, constructed from it's children
    children can be of same type or of ElementaryNode type
    """
    def __init__(self, children, minimum):
        self.children = children
        self.minimum = minimum/2.
        output = "("
        output += self.children[0].name
        output += ":"+str(self.minimum-self.children[0].minimum)
        output += ","
        output += self.children[1].name
        output += ":"+str(self.minimum-self.children[1].minimum)
        output += ")"
        #output += ":"+str(self.minimum/2.)
        self.name = output


def createNameList():
    """
    Creates a list with potential alphabetic names, starting with "A",
    ending with "ZZ", containing all possible variations.

    Input:
    none

    Output:
    nameList: python list object
    """
    nameList = []
    for i in range(ord("Z")+1-ord("A")):
        nameList.append(chr(ord("A")+i))
    for i in range(ord("Z")+1-ord("A")):
        for j in range(ord("Z")+1-ord("A")):
            nameList.append(chr(ord("A")+i)+chr(ord("A")+j))

    return nameList


def findMinimumValue(matrix):
    """
    Finds the minimum value in the lower triangulate of a square matrix,
    complexity O(nlog(n))

    Input:
    matrix: numpy matrix object

    Output:
    python list object containing the minimum value and its indices
    """
    minValue = matrix[0][1]
    minI = 0
    minJ = 1
    for i in range(len(matrix)):
        for j in range(i):
            if matrix[i][j] != 0 and matrix[i][j] < minValue:
                minValue = matrix[i][j]
                minI = i
                minJ = j

    return [minValue, minI, minJ]


def readMatrixfromFile(filename):
    """
    Reads from an input file and creates a numpy matrix object.

    Input:
    file: python file object

    Output:
    matrix: numpy matrix object
    """
    return np.loadtxt(filename)


def jukesCantor(fileObject, typeOfInput):
    """
    Reads from an input file containing aligned sequences and creates a dissimilarity matrix.
    Uses the Jukes Cantor (JC69) DNA Model
    Input:
    file: python file object

    Output:
    matrix: numpy matrix object
    """
    sequences = []
    nameList = []
    tmpSequence = ""
    if typeOfInput == "FASTA":
        for line in fileObject:
            if line[0] == '>':
                nameList.append(line.strip(">"))
                if tmpSequence != "":
                    sequences.append(tmpSequence)
                    tmpSequence = ""
            else:
                tmpSequence += line.strip()
        sequences.append(tmpSequence)

    else:
        for line in fileObject:
            sequences.append(line.strip())


    sequenceLength = len(sequences[0])
    numberOfSequences = len(sequences)

    matrix = np.zeros((numberOfSequences, numberOfSequences))

    #print numberOfSequences

    for i in range(numberOfSequences-1):
        for j in range(i+1,numberOfSequences):
            numdiff = 0
            for index in range(sequenceLength):
                if sequences[i][index] != sequences[j][index]:
                    numdiff += 1

            if (float(numdiff)/float(sequenceLength)) >= 0.75:
                return "Error"
            distance = -0.75 * math.log(1 - (4./3.)*(float(numdiff) / sequenceLength), math.e)
            matrix[i][j] = distance
            matrix[j][i] = distance
        print i

    return matrix, nameList

def kimura(fileObject, typeOfInput):
    """
    Reads from an input file containing aligned sequences and creates a dissimilarity matrix.
    Uses the Kimura (K80) DNA Model

    Input:
    file: python file object

    Output:
    matrix: numpy matrix object
    """
    sequences = []
    nameList = []
    tmpSequence = ""

    if typeOfInput == "FASTA":
        for line in fileObject:
            if line[0] == '>':
                nameList.append(line.strip(">"))
                if tmpSequence != "":
                    sequences.append(tmpSequence)
                    tmpSequence = ""
            else:
                tmpSequence += line.strip()
        sequences.append(tmpSequence)

    else:
        for line in fileObject:
            sequences.append(line.strip())


    sequenceLength = len(sequences[0])
    numberOfSequences = len(sequences)

    matrix = np.zeros((numberOfSequences, numberOfSequences))

    for i in range(numberOfSequences-1):
        for j in range(i+1, numberOfSequences):
            numTransitions = 0.
            numTransversions = 0.
            for index in range(sequenceLength):
                case = ((sequences[i][index] == 'A' and sequences[j][index] == 'G') or
                        (sequences[i][index] == 'G' and sequences[j][index] == 'A') or
                        (sequences[i][index] == 'T' and sequences[j][index] == 'C') or
                        (sequences[i][index] == 'C' and sequences[j][index] == 'T'))
                if case:
                    numTransitions += 1
                elif sequences[i][index] == "-" or sequences[j][index] == "-" or sequences[i][index]==sequences[j][index]:
                    pass
                else:
                    numTransversions += 1

            numTransversions /= sequenceLength
            numTransitions /= sequenceLength
            distance = -0.5*math.log(1-numTransversions-2*numTransitions, math.e) - 0.25*math.log(1-2*numTransversions,
                                                                                                  math.e)
            matrix[i][j] = distance
            matrix[j][i] = distance
        print i

    return matrix, nameList