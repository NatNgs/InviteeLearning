from sklearn.ensemble import RandomForestClassifier
from sklearn.externals import joblib # save RandomForest
from numpy import genfromtxt, savetxt

def train():
	print "Training..."
	
	#create the training & test sets, skipping the header row with [1:]
	dataset = genfromtxt(open('Data/donnees.csv','r'), delimiter=';')[1:]
	target = [x[0] for x in dataset]
	train = [x[1:] for x in dataset]

	#create and train the random forest
	
	#multi-core CPUs can use: rf = RandomForestClassifier(n_estimators=100, n_jobs=2)
	#n_estimators = number of trees
	#n_jobs = number of processes to run
	rf = RandomForestClassifier(n_estimators=100, n_jobs=4)
	rf.fit(train, target) # Learning
	
	# Save RandomForest
	joblib.dump(rf, 'Data/randomForestSave.pkl')
	
	print "Training complete !"
	

def test():
	print "Testing..."
	
	# Load randomForest
	rf = joblib.load('Data/randomForestSave.pkl')
	
	# Load data file
	dataset = genfromtxt(open('Data/test.csv','r'), delimiter=';')[1:]
	target = [x[0] for x in dataset]
	train = [x[1:] for x in dataset]
	
	# Predict
	prediction = rf.predict(train)
	
	# Compute win/fail ratio
	win = 0
	fail = 0
	for i in range(0, len(target)):
		if target[i] == prediction[i]:
			win = win + 1
		else:
			fail = fail + 1
			
	ratio = 0
	if win + fail > 0:
		ratio = 100.0*win/(win+fail)
	
	print "Test complete with %2f%% precision ( %d correct, %d failed )" % (ratio, win, fail)
	

# Main

if __name__ == '__main__':
	train()
	test()


