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
PKGDIR=$APPNAME-bin

# Create the packaging directory
mkdir -p $PKGDIR

# Copy Files
cp target/lc19-*-full.jar $PKGDIR
chmod 755 $PKGDIR/lc19-*-full.jar
cp app.properties $PKGDIR
cp LICENSE.txt $PKGDIR
cp serverkeystore.jks $PKGDIR
mkdir $PKGDIR/public
cp public/index.gmi $PKGDIR/public

# Zip up the directory
zip -r $PKGDIR.zip $PKGDIR

# Remove the packaging dir
rm -r -f $PKGDIR
