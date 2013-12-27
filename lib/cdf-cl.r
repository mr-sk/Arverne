
data <- read.csv("tradedata/USDJPY_hour.csv")
tsData = ts(data[,4])
cdf = quantile(diff(tsData), 0:100/100)
indices <- 0:100/100
probability <- function(x) { if (x < 0) { max(indices[cdf < x]) } else { 1 - min(indices[cdf > x]) }  }

f <- function(currentPrice, strikePrice, optionCost) {
  diff = strikePrice - currentPrice
  probOfFailure = probability(diff)
  probOfSuccess = 1 - probOfFailure

  expectedEarnings = 100 - optionCost
  expectedLosses   = optionCost

  return ((probOfSuccess * expectedEarnings) - (probOfFailure * expectedLosses))
}

arg <- commandArgs(trailingOnly = TRUE)
f(as.integer(arg[1]), as.integer(arg[2]), as.integer(arg[3]));