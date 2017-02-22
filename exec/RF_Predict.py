from sklearn.ensemble import RandomForestClassifier
from sklearn.externals import joblib # save RandomForest
from numpy import genfromtxt, savetxt

def predict():
	print "Predicting..."
	
	# Load randomForest
	rf = joblib.load('Data/randomForestSave.pkl')
	
	# Load data file
	toPredict = genfromtxt(open('Data/unknown.csv','r'), delimiter=';')[1:]
	
	# Predict
	prediction = rf.predict(toPredict)
	
	# Save result
	savetxt('Data/predictions.csv', prediction)
	
	print "Predict complete !"
	return prediction


# Main

if __name__ == '__main__':
	print predict()

