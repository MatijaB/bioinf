#!/usr/bin/python

from upgma_lib import *
import os
import time
import argparse

def main(distanceFunction, path, type):
    """
    Creates a newick tree from a number of aligned sequences

    Input:
    distanceFunction: the descriptor of the distance function from upgma_lib used to calculate the dis. matrix
    path: path to the input file containing aligned sequences
    type: type of file, either "FASTA" or "SEQ"

    Output:
    newickString: a string containing the newick tree in string format
    """
    txtfile = open(path, 'r+')
    start = time.time()
    matrix, nameList = distanceFunction(txtfile, type)
    end = time.time()
    timeElapsed = (str(end-start)).split(".")
    print "Trajanje "+distanceFunction.__name__+": "+timeElapsed[0]+" sec"
    start = time.time()

    if len(nameList) == 0:
        nameList = createNameList()

    #returns an error if the number of different characters between two sequences is over 75% of all characters
    if matrix == "Error":
        print "Greska"
        return

    #else:
    #    print matrix

    #names the existing original nodes with lowercase letters from a to z
    # (goes on in the ASCII if more than 28 nodes)
    nodesArray = [ElementaryNode(nameList[i]) for i in range(len(matrix))]

    #print [node.name for node in nodesArray]

    while len(matrix) > 2:

        minimum = findMinimumValue(matrix)

        #calculates dissimilarity values between the newly-constructed cluster and all other nodes
        #as Dij = (Di+Dj)/2
        newClusterValues = []
        for i in range(len(matrix)):
            if i != minimum[1] and i != minimum[2]:
                newClusterValues.append((matrix[minimum[1]][i]+matrix[minimum[2]][i]) / 2)

        #puts the new dissimilarity values of the created cluster where the values of
        #one of the two cluster-forming members used to be in the matrix
        counter = 0
        for i in range(len(matrix)):
            if i != minimum[2] and i != minimum[1]:
                matrix[minimum[2]][i] = newClusterValues[counter]
                matrix[i][minimum[2]] = newClusterValues[counter]
                counter += 1

        #construct the cluster name as a compound of former names, while making sure it's in alphabet order
        children = []
        if ord(nodesArray[minimum[2]].name[0]) < ord(nodesArray[minimum[1]].name[0]):
            #newClusterLetter= nodesArray[minimum[2]]+nodesArray[minimum[1]]
            children.append(nodesArray[minimum[2]])
            children.append(nodesArray[minimum[1]])
        else:
            #newClusterLetter= nodesArray[minimum[1]]+nodesArray[minimum[2]]
            children.append(nodesArray[minimum[1]])
            children.append(nodesArray[minimum[2]])

        nodesArray[minimum[2]] = NewickNode(children, minimum[0])
        nodesArray.remove(nodesArray[minimum[1]])

        #removes the row and column of the other member of the newly-formed cluster, thereby
        #reducing the matrix size by one
        matrix = np.delete(matrix, minimum[1],0)
        matrix = np.delete(matrix, minimum[1],1)

        #print [node.name for node in nodesArray]

    lastNode = NewickNode(nodesArray, matrix[0][1])

    end = time.time()
    timeElapsed = (str(end-start)).split(".")
    print "Trajanje UPGMA: "+timeElapsed[0]+" sec"

    return lastNode.name + ";"


if __name__ == '__main__':

    parser = argparse.ArgumentParser(
        description="Creates and shows a Newick tree from a file containing aligned sequences in either multiFASTA"
                    " format or a txt file containing one sequence in each file (FASTA or SEQ).\n Default run can be"
                    " done without any additional arguments. The default file is defaultTestFile.fasta, and the default"
                    " model is Jukes-Cantor (JC69). Default run time around 2 minutes."
    )
    parser.add_argument('--file', type=str, default="defaultTestFile.fasta", help="Path to the input file.")
    parser.add_argument('--type', type=int, default=0, help="0 for FASTA, 1 for SEQ filetype. FASTA is the default type.")
    parser.add_argument('--model', type=int, default=0, help="0 for JC69, 1 for K80.")

    args = parser.parse_args()

    if args.type == 0:
        type = "FASTA"
    elif args.type == 1:
        type = "SEQ"
    else:
        print "Error in arguments, exiting now!"
        exit()

    start = time.time()
    if args.model == 0:
        print "JC69"
        newickString = main(kimura, args.file, type)
    elif args.model == 1:
        print "K80"
        newickString = main(jukesCantor, args.file, type)
    else:
        print "Error in arguments, exiting now!"
        exit()

    NewFile = open("newick.txt", 'w+')

    NewFile.write(newickString)

    NewFile.close()

    end = time.time()
    timeElapsed = (str(end-start)).split(".")
    print "Ukupno trajanje: "+timeElapsed[0]+" sec"

    os.system("njplot newick.txt")

