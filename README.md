# Example widget bundle project for HABPanel

This sample project contains the source to an OSGi bundle provisioning a widget for [HABPanel](https://github.com/openhab/org.openhab.ui.habpanel) and its associated resources.

It can be built with Maven (`mvn clean package`), and installed like another bundle (for instance, drop in openHAB's `addons` folder).

This bundle showcases a few things that can be achieved with this approach:

- If it detects an already present HABPanel configuration, it provisions a widget from its JSON file through it, making it available for use;
- It registers a few icons as an ESH iconset, those are used by the widget's template;
- It maps a CSS file as a static resource in the server so the widget can load it with the `oc-lazy-load` directive, using classes and avoiding inline styles. Note that AngularJS controllers, directives or even external Javascript libraries could be loaded that way!

This example project uses icons from the [knx-user-forum.de icon set](https://github.com/OpenAutomationProject/knx-uf-iconset) and is therefore distributed under the same license (CC BY-SA 3.0, see LICENSE) as required.
