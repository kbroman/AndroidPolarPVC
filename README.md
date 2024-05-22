# PolarPVC

This is a revision of the [KE.Net
ECG](https://github.com/KennethEvans/KE.Net-ECG) Android application,
which implements getting an ECG from a Polar H10 heart monitor.
See
<https://kenevans.net/opensource/KEDotNetECG/Assets/kedotnetecg.html>
for more information.

The modifications are to identify [Premature Ventricular Complexes (PVCs)](https://en.wikipedia.org/wiki/Premature_ventricular_contraction)
and to estimate the current approximate percent PVC.

The H10 needs to have firmware version 3.0.35 or later installed, and
you need to know the ID of your device (which is printed on its side).
You can see the ECG trace in real time and save PNG and CSV files of
the data.

It uses the [Polar SDK](https://github.com/polarofficial/polar-ble-sdk and
[Android Plot](https://github.com/halfhp/androidplot).

**More information**

More information and FAQ are at <https://kennethevans.github.io> as well as more projects from the same author.

Licensed under the MIT license. (See: <https://en.wikipedia.org/wiki/MIT_License>)
