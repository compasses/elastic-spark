#encoding=utf-8

"""
https://jeremykun.com/2016/05/16/singular-value-decomposition-part-2-theorem-proof-algorithm/
1. space transformation;
2. From high space, to low space also same to basis switch. but keep more important basis.
"""
import logging
from numpy.linalg import svd

logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s', level=logging.INFO)

movieRatings = [
    [2, 5, 3],
    [1, 2, 1],
    [4, 1, 1],
    [3, 5, 2],
    [5, 3, 1],
    [4, 5, 5],
    [2, 4, 2],
    [2, 2, 5],
]

U, singularValues, V = svd(movieRatings)

logging.info("u values and v%s", U)