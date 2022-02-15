#!/bin/bash
#
# Copyright 2022 Austin Lehman. (cup_of_code@fastmail.com)
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Lesser General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.
#

# App name
APPNAME=lc19

# Run maven package
mvn clean package
if [[ "$?" -ne 0 ]] ; then
  echo 'Maven build failed, exiting'; exit $rc
fi

# Packaging Dir
PKGDIR=/tmp/$APPNAME

# Create the packaging directory
mkdir -p $PKGDIR/DEBIAN

# Copy Files
mkdir -p $PKGDIR/opt/lc19
cp target/lc19-*-full.jar $PKGDIR/opt/lc19
chmod 755 $PKGDIR/opt/lc19/lc19-*-full.jar
cp app.properties $PKGDIR/opt/lc19
cp LICENSE.txt $PKGDIR/opt/lc19
cp serverkeystore.jks $PKGDIR/opt/lc19
mkdir $PKGDIR/opt/lc19/public
cp public/index.gmi $PKGDIR/opt/lc19/public

# Copy debian files
cp -f package-files/control $PKGDIR/DEBIAN
cp -f package-files/postinst $PKGDIR/DEBIAN

# Copy systemd files
mkdir -p $PKGDIR/etc/systemd/system/
cp -R -f package-files/systemd/* $PKGDIR/etc/systemd/system

# Create the package
dpkg-deb --build $PKGDIR

# Copy deb file back here
cp $PKGDIR.deb .

# Remove the packaging dir
rm -r -f $PKGDIR