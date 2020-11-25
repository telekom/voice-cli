# How should I adopt the file structure to my needs:

By initializing your repository the TOSCom has generated some useful documents. Some of them are mandatory and must not be changed, some of them are mandatory, but should be attuned to your needs, and some of them are optional. Here is a description of the documents, their purposes, and what you may/should (not) do with them:

* **4-THE-REPO-OWNER.md**: this file.
* **CODE_OF_CONDUCT.md**: some well-known rules how contributors should behave.
  - You may erase this file if you do not need it.
  - If you are going to use it, please replace the contact address ``opensource@telekom.de`` with your own address.
* **CODEOWNERS**: a list of persons denoted by their GitHub accounts who should handle pull requests.
  - You may erase this file if you do not need it - but to have such a file (and the respective persons) is a good practice if you use GitHub.
  - Append your code owners identifiers to the initially inserted person @kreincke.
  - Erase the initially inserted codeowner ``kreincke``.
* **codestyle/checkstyle.xml**: a description of how to write source code.
  - You may erase or replace this file in accordance with your needs.
* **CONTRIBUTING.md**: a description of how the community can contribute to your project.
  - You may erase this file if you do not need it.
  - If you delete it, find another method (CLA etc.) to ensure your contributors also license their work under the terms of the same license you've selected for your project.
  - If you are going to use it, please replace the contact address ``opensource@telekom.de`` with your own address.
* **LICENSE**: The license text of and for your project.
  - _YOU MAY NOT DELETE THIS FILE!_
 * **README.md**: the general project description file.
  - _YOU MAY NOT DELETE THIS FILE!_
  - _YOU MAY NOT DELETE THE LICENSING STATEMENT AT THE END OF THE FILE!_
  - You may and should adopt the content to your needs. The initially inserted structure is best practice.
* **SECURITY.md**: Contains instructions to which e-mail address vulnerabilities should be made known.
  - You may erase this file if you do not need it.
  - If you are going to use it, please replace the contact address ``opensource@telekom.de`` with your own address.
* **THIRD-PARTY-NOTICES**: a list of dependencies
* **templates/fileheader.txt**: a template for initializing any new files.
  - Each file must contain an instantiated version of the fileheader.txt
  - You must adopt the copyright line to your reality.
