framework_2624: Un framework web léger pour vos projets Java
framework_2624 est un framework web Java minimaliste conçu pour faciliter la création d'applications web dynamiques. Il s'inspire de concepts similaires à Spring MVC, tout en restant léger et flexible.

Fonctionnalités principales
    Annotation @Controller : Identifie les classes contenant des points d'entrée pour les requêtes HTTP.
    Méthodes de contrôleur avec annotation @Get : Gère les requêtes HTTP GET. (D'autres annotations pour les méthodes POST, PUT, etc. peuvent être implémentées ultérieurement)
    Gestionnaire de requêtes frontal (Front Controller) : Intercepte les requêtes entrantes, identifie le contrôleur et la méthode appropriés, et exécute le code associé.
    Renvoi de chaînes de caractères : Permet aux méthodes de contrôleur de renvoyer du contenu HTML brut.
    Prise en charge de ModelAndView : Offre la possibilité de renvoyer des objets ModelAndView contenant des données de modèle et des vues JSP à utiliser pour le rendu.
    Analyse de package : Identifie automatiquement les classes de contrôleur annotées dans un package spécifié.

Démarrage rapide
    Ajoutez le framework à votre projet : mettez le Framework.jar dans le lib de votre projet

Créez des classes de contrôleur :
    Marquez vos classes de contrôleur avec l'annotation @Controller.
    Définissez des méthodes pour gérer les requêtes HTTP.
    Annotez les méthodes de contrôleur avec @Get pour gérer les requêtes GET. (D'autres annotations pour les méthodes POST, PUT, etc. peuvent être implémentées ultérieurement)
    Ces méthodes peuvent renvoyer du contenu HTML brut en tant que chaîne de caractères ou renvoyer un objet ModelAndView.

Configurez le FrontController :
    Dans le fichier web.xml de votre application web, mappez le FrontController à une URL spécifique (par exemple, *.do).
    Définissez les paramètres d'initialisation suivants dans web.xml :
        controllerPackage: Le package contenant vos classes de contrôleur.  
        viewFolder: Le dossier de base où se trouvent vos vues JSP (par exemple, "/WEB-INF/views/").
        suffixe: Le suffixe des fichiers de vue JSP (par exemple, ".jsp").

Points à retenir
    Ce framework est un projet de base et peut être étendu pour inclure des fonctionnalités supplémentaires telles que la gestion des formulaires, la validation des entrées, et la prise en charge d'autres méthodes HTTP.
N'oubliez pas de configurer les paramètres d'initialisation du FrontController dans web.xml pour indiquer l'emplacement de vos classes de contrôleur et de vos vues JSP.

Licence
    framework_2624 est distribué sous une licence libre permissive . Vous êtes libre de l'utiliser, de le modifier et de le redistribuer selon les termes de la licence choisie.