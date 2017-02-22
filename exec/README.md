Pour faire fonctionner correctement le 'autofeed.sh', il faut que ce dossier soit composé comme suivant:

--+ exec
  |
  |--+ Data
  |  |
  |  |--+ LearnCuts           	Dossier utilisé pour faire apprendre la RandomForest
  |  |  |
  |  |  |--+ 0					Dossier contenant plusieurs fichiers de logs de coupes de qualité 0, utilisés pour l'apprentissage
  |  |  |--+ 1					Dossier contenant plusieurs fichiers de logs de coupes de qualité 1, utilisés pour l'apprentissage
  |  |  |--+ 2					Dossier contenant plusieurs fichiers de logs de coupes de qualité 2, utilisés pour l'apprentissage
  |  |  |--+ 3					Dossier contenant plusieurs fichiers de logs de coupes de qualité 3, utilisés pour l'apprentissage
  |  |  |
  |  | 
  |  |--+ TestsCuts           	Dossier utilisé pour tester que l'apprentissage s'est correctement déroulé
  |  |  |
  |  |  |--+ 0					Dossier contenant plusieurs fichiers de logs de coupes de qualité 0, utilisés pour vérifier la qualité de l'apprentissage
  |  |  |--+ 1					Dossier contenant plusieurs fichiers de logs de coupes de qualité 1, utilisés pour vérifier la qualité de l'apprentissage
  |  |  |--+ 2					Dossier contenant plusieurs fichiers de logs de coupes de qualité 2, utilisés pour vérifier la qualité de l'apprentissage
  |  |  |--+ 3					Dossier contenant plusieurs fichiers de logs de coupes de qualité 3, utilisés pour vérifier la qualité de l'apprentissage
  |  |  |
  |  | 
  |  |--+ UnknownCuts         	Dossier contenant plusieurs fichiers de logs de coupes dont on veut prédire la qualité
  |  | 
  |  | 
  |  |--- learn.csv           	Fichier généré par 'autofeed.sh' et utilisé par 'RF_Learn.py' pour nourir la RandomForest
  |  |--- test.csv            	Fichier généré par 'autofeed.sh' et utilisé par 'RF_Learn.py' pour tester la RandomForest
  |  |--- unknown.csv         	Fichier généré par 'autofeed.sh' et utilisé par 'RF_Predict.py' pour prédire les valeurs
  |  |--- predictions.csv     	Fichier généré par 'RF_Predict.py' détaillant les prédictions des fichiers de coupes de qualité inconnues
  |  |--- randomForestSave.pkl	Fichier généré par 'RF_Learn.py', contenant une sauvegarde de la RandomForest
  |  |
  |
  |--- compileAll.sh	Script pour générer 'feeder.jar'
  |--- feed.sh      	Script manuel pour générer un fichier .csv
  |--- autofeed.sh  	Script automatique pour générer les 3 fichiers learn.csv, test.csv et unknown.csv
  |--- feeder.jar   	Programme pour fabriquer les 3 fichiers .csv
  |--- RF_Learn.py  	Programme pour générer et nourir la RandomForest, puis qui la sauvegarde dans 'randomForestSave.pkl'
  |--- RF_Predict.py	Programme utilisant 'randomForestSave.pkl' et 'unknown.csv' pour prédire les valeurs des coupes de qualité inconnues
  |
