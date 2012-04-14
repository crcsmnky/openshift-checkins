# Red Hat OpenShift Checkins App

## Getting Started

Clone the source and build the app. You'll need [Java](http://www.java.com/en/download/manual.jsp) and [Maven](http://maven.apache.org) in order to build. It probably wouldn't hurt to have download and install [MongoDB](http://www.mongodb.org/downloads) either.

    $ git clone http://github.com/crcsmnky/openshift-checkins.git
    $ cd openshift-checkins
    $ mvn package

To deploy the app you'll need an [OpenShift](https://openshift.redhat.com/app/) account and install the tools:

    $ gem install rhc
    
Now you can deploy the ``checkins.war`` file to OpenShift. Once deployed, test it like so:

    $ curl -X POST -d "comment=hello&x=1&y=1" http://appurl.rhcloud.com/checkin
