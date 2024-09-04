framework_2624 est un framework web Java minimaliste conçu pour simplifier la création d'applications web dynamiques, en s'inspirant de concepts de Spring MVC tout en restant léger et flexible. Il propose les fonctionnalités suivantes :

@Controller : Annotation pour identifier les classes qui contiennent des points d'entrée pour les requêtes HTTP.
@Get : Annotation pour gérer les requêtes HTTP GET (d'autres annotations pour POST, PUT, etc., peuvent être ajoutées).
Front Controller : Gère les requêtes entrantes, identifie le contrôleur et la méthode appropriés, puis exécute le code associé.
Renvoi de chaînes de caractères : Les méthodes de contrôleur peuvent renvoyer du contenu HTML brut.
ModelAndView : Permet de renvoyer des objets contenant des données de modèle et des vues JSP.
Analyse de package : Identifie automatiquement les classes de contrôleur dans un package spécifié.
Démarrage rapide :

Ajoutez Framework.jar à votre projet.
Créez des classes de contrôleur annotées avec @Controller et gérez les requêtes avec @Get.
Configurez le FrontController dans web.xml, en spécifiant le package des contrôleurs, le dossier des vues JSP, et le suffixe des fichiers de vue.
Points à retenir : Le framework est basique mais extensible, avec la possibilité d'ajouter des fonctionnalités supplémentaires comme la gestion des formulaires et la validation des entrées.
