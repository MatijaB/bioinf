import networkx as nx
import numpy as np
import time

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

