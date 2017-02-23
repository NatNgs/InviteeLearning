Toutes les opérations sont a effectuer depuis le dossier .../exec/
(dans lequel devrait se trouver ce fichier README.md)


Préparation

	1: Compiler le normaliseur Java et créer l'arborescence de dossiers
			$ ./compileAll.sh

	2: Ajouter des fichiers d'enregistrement de coupes dont la qualité est connue, dans les différents dossiers
		> Dans le dossier Data/LearnCuts, dans le sous-dossier correspondant à la qualité de la coupe, pour enrichir l'algorithme de prédiction.
		 
		> Dans le dossier Data/TestsCuts, dans le sous-dossier correspondant à la qualité de la coupe, pour améliorer la précision du calcul de validité de l'algorithme de prédiction

	3: Executer le script d'apprentissage de l'algorithme de prédiction
			$ ./RF_Learn.sh



Nettoyage des fichiers (il faudra refaire l'étape de préparation après celle-ci pour pouvoir réutiliser l'algorithme de prédiction)

	1: Executer le script clean.sh
			$ ./clean.sh
			
	2: Supprimer le dossier Data
		/!\ prenez bien soin d'avoir une copie des enregistrements de coupe qui se trouvent dans les dossiers LearnCuts, TestCuts et UnknownCuts, ceux-ci seront définitivement supprimés.
			$ rm -rf Data/




Détail de l'arborescence des fichiers

--+ exec
  |
  |--+ Data
  |  |
  |  |--+ LearnCuts           	Dossier utilisé pour faire apprendre la RandomForest
  |  |  |
  |  |  |--+ 0		Dossier contenant plusieurs fichiers de logs de coupes de qualité 0, utilisés pour l'apprentissage
  |  |  |--+ 1		Dossier contenant plusieurs fichiers de logs de coupes de qualité 1, utilisés pour l'apprentissage
  |  |  |--+ 2		Dossier contenant plusieurs fichiers de logs de coupes de qualité 2, utilisés pour l'apprentissage
  |  |  |--+ 3		Dossier contenant plusieurs fichiers de logs de coupes de qualité 3, utilisés pour l'apprentissage
  |  |  |
  |  | 
  |  |--+ TestsCuts           	Dossier utilisé pour tester que l'apprentissage s'est correctement déroulé
  |  |  |
  |  |  |--+ 0			Dossier contenant plusieurs fichiers de logs de coupes de qualité 0, utilisés pour vérifier la qualité de l'apprentissage
  |  |  |--+ 1			Dossier contenant plusieurs fichiers de logs de coupes de qualité 1, utilisés pour vérifier la qualité de l'apprentissage
  |  |  |--+ 2			Dossier contenant plusieurs fichiers de logs de coupes de qualité 2, utilisés pour vérifier la qualité de l'apprentissage
  |  |  |--+ 3			Dossier contenant plusieurs fichiers de logs de coupes de qualité 3, utilisés pour vérifier la qualité de l'apprentissage
  |  |  |
  |  |
  |  |--- learn.csv           	Fichier généré par 'RF_Learn.sh' et utilisé par 'RF_Learn.py' pour nourir la RandomForest
  |  |--- test.csv            	Fichier généré par 'RF_Learn.sh' et utilisé par 'RF_Learn.py' pour tester la RandomForest
  |  |--- randomForestSave.pkl	Fichier généré par 'RF_Learn.py', contenant une sauvegarde de la RandomForest
  |  |
  |
  |--- clean.sh      	Script pour effacer tous les fichiers automatiquement générés (annule le compileAll et le RF_Learn)
  |--- compileAll.sh 	Script pour générer 'feeder.jar' et l'arborescence de dossiers
  |--- RF_Learn.sh   	Script pour préparer et executer le programme RF_Learn.py
  |--- RF_Learn.py   	Programme pour générer et nourir la RandomForest, puis qui la sauvegarde dans 'randomForestSave.pkl'
  |--- README.md     	Ce fichier
  |
