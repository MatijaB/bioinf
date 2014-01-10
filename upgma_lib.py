import networkx as nx
import numpy as np
import time
import math

class ElementaryNode():
    def __init__(self,name):
        self.name = name
        self.minimum = 0

class NewickNode():
    def __init__(self, children, minimum):
        self.children = children
        self.minimum = minimum/2
        output = "("
        output += self.children[0].name
        output += ":"+str(self.minimum-self.children[0].minimum)
        output += ","
        output += self.children[1].name
        output += ":"+str(self.minimum-self.children[1].minimum)
        output += ")"
        #output += ":"+str(self.minimum/2.)
        self.name = output

def namingList():
    list = []
    for i in range(ord("Z")+1-ord("A")):
        list.append(chr(ord("A")+i))
    for i in range(ord("Z")+1-ord("A")):
        for j in range(ord("Z")+1-ord("A")):
            list.append(chr(ord("A")+i)+chr(ord("A")+j))

    return list


def findMinimumValue(matrix):
    """
    Finds the minimum value in the lower triangulate of a square matrix,
    complexity O(nlog(n))

    Input:
    matrix: numpy matrix object

    Output:
    python list object containing the minimum value and its indices
    """
    min = matrix[0][1]
    minI = 0
    minJ = 1
    for i in range(len(matrix)):
        for j in range(i):
            if matrix[i][j] != 0 and matrix[i][j] < min:
                min = matrix[i][j]
                minI = i
                minJ = j

    return [min,minI,minJ]

def readGraphFromMatrix(matrix):
    """
    Reads from the dissimilarity matrix and builds a connected graph from it,
    adding dissimilarity as an edge attribute

    Input:
    matrix: numpy matrix object

    Output:
    graph: networkx graph object
    """
    matrix.dtype = [('diss',float)]

    return nx.from_numpy_matrix(matrix)

def readMatrixfromFile(file):
    """
    Reads from an input file and creates a numpy matrix object.

    Input:
    file: python file object

    Output:
    matrix: numpy matrix object
    """
    return np.loadtxt(file)

def jukesCantor(file,type):
    """
    Reads from an input file containing aligned sequences and creates a dissimilarity matrix.

    Input:
    file: python file object

    Output:
    matrix: numpy matrix object
    """
    sequences = []
    start = 0
    tmpSequence = ""
    if type == "FASTA":
        for line in file:
            if line[0] == '>':
                if tmpSequence != "":
                    sequences.append(tmpSequence)
                    tmpSequence = ""
            else:
                tmpSequence += line.strip()
    else:
        for line in file:
            sequences.append(line.strip())

    matrix = np.zeros((len(sequences),len(sequences)))

    i=0
    j=0

    for sequence1 in sequences:
        j = 0
        for sequence2 in sequences:
            if sequence1 == sequence2:
                matrix[i][j] = 0.
            else:
                numdiff = 0
                for index in range(len(sequence1)):
                    if sequence1[index] != sequence2[index]:
                        numdiff += 1

                if (float(numdiff)/len(sequence1)) >= 0.75:
                    return "Error"

                distance = -0.75 * math.log(1- (4./3.)*(float(numdiff) / len(sequence1)), math.e)
                matrix[i][j] = distance
                matrix[j][i] = distance

            j += 1

        i += 1

    return matrix