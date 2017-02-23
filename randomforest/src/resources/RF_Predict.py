from sklearn.ensemble import RandomForestClassifier
from sklearn.externals import joblib # save RandomForest
import numpy as np

def predict(rf_file, to_predict_vector):
	# Load random_forest
	rf = joblib.load(rf_file)  # 'randomForestSave.pkl'
	
	# Load data file
	to_predict = np.array(map(int, to_predict_vector.split())).reshape((1, -1))
	prediction = rf.predict(to_predict)

	# Predict
	for value in prediction:
		print value, # print values separated by spaces

	print "" # print newLine


# Main
if __name__ == '__main__':
	# Get randomForestSave file
	input_rf_file = raw_input()
	input_vector = raw_input()

	predict(input_rf_file, input_vector)

