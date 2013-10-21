from upgma_lib import *

def main():
    txtfile = open("matrix.txt")
    matrix = readMatrixfromFile(txtfile)

    nodesArray = [str(unichr(97+i)) for i in range(len(matrix))]

    print nodesArray

    while(len(matrix)>2):

        minimum = findMinimumValue(matrix)

        newClusterValues = []

        for i in range(len(matrix)):
            if i != minimum[1] and i != minimum[2]:
                newClusterValues.append( (matrix[minimum[1]][i]+matrix[minimum[2]][i]) / 2 )

        counter = 0


        for i in range(len(matrix)):
            if i != minimum[2] and i!= minimum[1]:
                matrix[minimum[2]][i] = newClusterValues[counter]
                matrix[i][minimum[2]] = newClusterValues[counter]
                counter += 1

        newClusterLetter= nodesArray[minimum[2]]+nodesArray[minimum[1]]

        nodesArray[minimum[2]] = newClusterLetter
        nodesArray.remove(nodesArray[minimum[1]])

        matrix = np.delete(matrix,minimum[1],0)
        matrix = np.delete(matrix,minimum[1],1)

        print nodesArray

    return

if __name__ == '__main__':
    start = time.time()
    main()
    end = time.time()
    print end - start
